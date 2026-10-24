const axios = require('axios');
const cheerio = require('cheerio');
const zlib = require('zlib');
const {
  cleanTitle,
  areTitlesCompatible,
  parseXmltvDate,
  extractImageSrc,
  resolveDescriptions,
  sanitizeDescriptionText,
  normalizeForComparison
} = require('./normalizer');

const HTTP_HEADERS = {
  'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
  'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
  'Accept-Language': 'fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7'
};

const detailCache = new Map();
const pageHtmlCache = new Map();

async function loadXmltvReference(channels) {
  const timeMap = new Map();
  const titleMap = new Map();
  const channelProgramsMap = new Map();
  const latestSeasonMap = new Map();

  const resolveChannelMap = ($root) => {
    const map = new Map();
    $root('channel').each((_, el) => {
      const rawId = $root(el).attr('id');
      const dispName = cleanTitle($root(el).find('display-name').text());
      const matched = channels.find(c => {
        const cName = cleanTitle(c.name);
        const cId = cleanTitle(c.id);
        return cName === dispName || cId === cleanTitle(rawId) || (cName.length > 3 && dispName.includes(cName)) || (dispName.length > 3 && cName.includes(dispName));
      });
      if (matched) {
        map.set(rawId, matched.id);
      }
    });
    return map;
  };

  const registerEntry = (channel, start, stop, desc, subTitle, title, rating, category, credits, season, episode, year, seriesYear, country, icon, channelMap, isUnreleased = false, isRerun = false, hasSubtitles = false, isHd = false) => {
    if (!channel || !start || !title) return;
    const entry = {
      channelId: channel,
      title,
      subTitle: subTitle || null,
      desc: desc || null,
      category: category || null,
      rating: rating || null,
      credits,
      season,
      episode,
      year,
      seriesYear,
      country,
      icon,
      isUnreleased,
      isRerun,
      hasSubtitles,
      isHd,
      start: parseXmltvDate(start),
      stop: parseXmltvDate(stop)
    };

    if (season) {
      const cTitle = cleanTitle(title);
      const currentMax = latestSeasonMap.get(cTitle) || 0;
      if (season > currentMax) latestSeasonMap.set(cTitle, season);
    }

    const targetChannels = [channel];
    const mapped = channelMap.get(channel);
    if (mapped && mapped !== channel) targetChannels.push(mapped);

    for (const ch of targetChannels) {
      if (entry.desc && entry.desc.length > 5) {
        const timeKey = `${ch}_${start.substring(0, 12)}`;
        timeMap.set(timeKey, entry);

        if (entry.subTitle) {
          const daySubKey = `${ch}_${start.substring(0, 8)}_${cleanTitle(title)}_${cleanTitle(entry.subTitle)}`;
          titleMap.set(daySubKey, entry);
        }

        const dayKey = `${ch}_${start.substring(0, 8)}_${cleanTitle(title)}`;
        if (!titleMap.has(dayKey)) {
          titleMap.set(dayKey, entry);
        }

        const generalKey = `${ch}_${cleanTitle(title)}`;
        if (!titleMap.has(generalKey)) {
          titleMap.set(generalKey, entry);
        }
      }

      if (entry.start) {
        const progDayKey = `${ch}_${start.substring(0, 8)}`;
        if (!channelProgramsMap.has(progDayKey)) {
          channelProgramsMap.set(progDayKey, []);
        }
        channelProgramsMap.get(progDayKey).push({ ...entry, channelId: ch });
      }
    }
  };

  try {
    const res = await axios.get('https://xmltvfr.fr/xmltv/xmltv_tnt.xml.gz', {
      responseType: 'arraybuffer',
      timeout: 20000,
      headers: HTTP_HEADERS
    });
    const xml = zlib.gunzipSync(res.data).toString('utf-8');
    const $xml = cheerio.load(xml, { xmlMode: true });
    const channelMap = resolveChannelMap($xml);

    $xml('programme').each((_, el) => {
      const channel = $xml(el).attr('channel');
      const start = $xml(el).attr('start');
      const stop = $xml(el).attr('stop');
      const desc = $xml(el).find('desc').text().trim();
      const subTitle = $xml(el).find('sub-title').text().trim();
      const title = $xml(el).find('title').text().trim();
      const rating = $xml(el).find('rating value').text().trim();
      const category = $xml(el).find('category').first().text().trim();
      const dateText = $xml(el).find('date').text().trim();
      const origDateText = $xml(el).find('orig-date').text().trim();
      const country = $xml(el).find('country').text().trim() || null;
      const icon = $xml(el).find('icon').attr('src') || null;

      const year = dateText ? parseInt(dateText.substring(0, 4), 10) : null;
      const seriesYear = origDateText ? parseInt(origDateText.substring(0, 4), 10) : null;

      let season = null;
      let episode = null;
      const epNumText = $xml(el).find('episode-num[system="xmltv_ns"]').text().trim();
      if (epNumText) {
        const parts = epNumText.split('.');
        if (parts[0]) season = parseInt(parts[0], 10) + 1;
        if (parts[1]) episode = parseInt(parts[1], 10) + 1;
      }

      const presenters = $xml(el).find('credits presenter').map((_, p) => $xml(p).text().trim()).get();
      const actors = $xml(el).find('credits actor').map((_, a) => $xml(a).text().trim()).get();
      const directors = $xml(el).find('credits director').map((_, d) => $xml(d).text().trim()).get();

      const isUnreleased = $xml(el).find('premiere').length > 0;
      const isRerun = $xml(el).find('previously-shown').length > 0;
      const hasSubtitles = $xml(el).find('subtitles').length > 0;
      const isHd = $xml(el).find('video quality').text().toUpperCase().includes('HD');

      registerEntry(channel, start, stop, desc, subTitle, title, rating, category, { presenters, actors, directors }, season, episode, year, seriesYear, country, icon, channelMap, isUnreleased, isRerun, hasSubtitles, isHd);
    });
  } catch (err) {
    process.stderr.write(`Failed to load xmltv_tnt reference: ${err.message}\n`);
  }

  try {
    const res = await axios.get('https://xmltvfr.fr/xmltv/xmltv.xml.gz', {
      responseType: 'arraybuffer',
      timeout: 30000,
      headers: HTTP_HEADERS
    });
    const xml = zlib.gunzipSync(res.data).toString('utf-8');
    const $xml = cheerio.load(xml, { xmlMode: true });
    const channelMap = resolveChannelMap($xml);

    $xml('programme').each((_, el) => {
      const channel = $xml(el).attr('channel');
      if (channelMap.has(channel)) {
        const start = $xml(el).attr('start');
        const stop = $xml(el).attr('stop');
        const desc = $xml(el).find('desc').text().trim();
        const subTitle = $xml(el).find('sub-title').text().trim();
        const title = $xml(el).find('title').text().trim();
        const rating = $xml(el).find('rating value').text().trim();
        const category = $xml(el).find('category').first().text().trim();
        const dateText = $xml(el).find('date').text().trim();
        const origDateText = $xml(el).find('orig-date').text().trim();
        const country = $xml(el).find('country').text().trim() || null;
        const icon = $xml(el).find('icon').attr('src') || null;

        const year = dateText ? parseInt(dateText.substring(0, 4), 10) : null;
        const seriesYear = origDateText ? parseInt(origDateText.substring(0, 4), 10) : null;

        let season = null;
        let episode = null;
        const epNumText = $xml(el).find('episode-num[system="xmltv_ns"]').text().trim();
        if (epNumText) {
          const parts = epNumText.split('.');
          if (parts[0]) season = parseInt(parts[0], 10) + 1;
          if (parts[1]) episode = parseInt(parts[1], 10) + 1;
        }

        const presenters = $xml(el).find('credits presenter').map((_, p) => $xml(p).text().trim()).get();
        const actors = $xml(el).find('credits actor').map((_, a) => $xml(a).text().trim()).get();
        const directors = $xml(el).find('credits director').map((_, d) => $xml(d).text().trim()).get();

        const isUnreleased = $xml(el).find('premiere').length > 0;
        const isRerun = $xml(el).find('previously-shown').length > 0;
        const hasSubtitles = $xml(el).find('subtitles').length > 0;
        const isHd = $xml(el).find('video quality').text().toUpperCase().includes('HD');

        registerEntry(channel, start, stop, desc, subTitle, title, rating, category, { presenters, actors, directors }, season, episode, year, seriesYear, country, icon, channelMap, isUnreleased, isRerun, hasSubtitles, isHd);
      }
    });
  } catch (err) {
    process.stderr.write(`Failed to load extended xmltv reference: ${err.message}\n`);
  }

  return { timeMap, titleMap, channelProgramsMap, latestSeasonMap };
}

async function fetchDetailPageData(link) {
  if (!link || (!link.startsWith('/programme/') && !link.startsWith('/cinema/') && !link.startsWith('/series/'))) return null;
  if (detailCache.has(link)) return detailCache.get(link);

  const cleanLink = link.split('#')[0];
  const fullUrl = `https://www.programme-tv.net${cleanLink}`;
  try {
    let pageHtml = pageHtmlCache.get(cleanLink);
    if (!pageHtml) {
      const res = await axios.get(fullUrl, { headers: HTTP_HEADERS, timeout: 8000 });
      pageHtml = res.data;
      pageHtmlCache.set(cleanLink, pageHtml);
    }
    const $page = cheerio.load(pageHtml);
    let desc = null;
    let season = null;
    let format = null;
    let genre = null;
    let year = null;
    let seriesYear = null;
    let country = null;
    let episodeNumber = null;
    let seriesDesc = null;
    const directors = [];

    const anchor = link.includes('#') ? link.split('#')[1].split('-')[0] : '';

    $page('script[type="application/ld+json"]').each((_, s) => {
      try {
        const json = JSON.parse($page(s).html());
        if (json.description && json.description.length > 20 && !json.description.endsWith('...') && !json.description.endsWith('…')) {
          desc = json.description.trim();
        }
        if (json.seasonNumber) {
          season = parseInt(json.seasonNumber, 10);
        }
        if (json.datePublished) {
          const matchY = String(json.datePublished).match(/(\d{4})/);
          if (matchY) year = parseInt(matchY[1], 10);
        }
        if (json.director) {
          const dirs = Array.isArray(json.director) ? json.director : [json.director];
          for (const d of dirs) {
            const name = typeof d === 'string' ? d : d?.name;
            if (name && typeof name === 'string' && name.trim()) {
              directors.push(name.trim());
            }
          }
        }
        if (json.episode && Array.isArray(json.episode)) {
          let epIdx = -1;
          for (let i = 0; i < json.episode.length; i++) {
            const e = json.episode[i];
            if (anchor && e.url && e.url.includes(anchor)) {
              epIdx = i;
              if (e.description && e.description.length > 20 && !e.description.endsWith('...') && !e.description.endsWith('…')) {
                desc = e.description.trim();
              }
              if (e.episodeNumber) episodeNumber = parseInt(e.episodeNumber, 10);
              break;
            }
          }
          if (episodeNumber == null && epIdx >= 0) {
            episodeNumber = epIdx + 1;
          }
        }
      } catch (parseErr) {
        if (process.env.DEBUG) console.error('JSON-LD parse error:', parseErr.message);
      }
    });

    let pageSyn = $page('.synopsis-text').first().text().replace(/\s+/g, ' ').trim();
    if (!pageSyn) {
      const synEl = $page('.synopsis, .program-summary').first().clone();
      synEl.find('button, .synopsis-action, a, [class*="action"], [class*="btn"]').remove();
      pageSyn = synEl.text().replace(/\s+/g, ' ').replace(/^R[ée]sum[ée]\s+[^:]*:\s*/i, '').replace(/Voir plusmoins/gi, '').trim();
    }
    if (pageSyn && pageSyn.length > 20 && !pageSyn.endsWith('...') && !pageSyn.endsWith('…')) {
      desc = pageSyn;
    }

    if (!desc) {
      const og = $page('meta[property="og:description"]').attr('content');
      if (og && og.length > 20 && !og.includes('Consultez la grille') && !og.endsWith('...') && !og.endsWith('…')) {
        desc = og.trim();
      }
    }

    const overviewTitle = $page('.overview-title').text().trim();
    const seasonMatch = overviewTitle.match(/Saison\s*(\d+)/i) || cleanLink.match(/saisons\/(\d+)/i);
    if (!season && seasonMatch) {
      season = parseInt(seasonMatch[1], 10);
    }

    let detailIsUnreleased = false;
    let detailIsRerun = false;
    let detailIsLive = false;
    let detailHasSubtitles = $page('.mainBroadcastCard-deaf, svg.icon-deaf, [aria-label*="Malentendant"]').length > 0 || /deaf/i.test($page('.overview, .synopsis').html() || '');
    let detailHasAudioDescription = $page('.mainBroadcastCard-audioDescription, svg.icon-audio-description, [aria-label*="Audio description"]').length > 0 || /audio-description|audiodescription/i.test($page('.overview, .synopsis').html() || '');
    let detailIsHd = false;
    let detailRating = null;

    $page('.overview-stickerListItem').each((_, li) => {
      const text = $page(li).text().replace(/\s+/g, ' ').trim();
      if (/in[ée]dit/i.test(text)) detailIsUnreleased = true;
      if (/rediff/i.test(text)) detailIsRerun = true;
      if (/\bdirect\b/i.test(text)) detailIsLive = true;
      if (/\b(st|sous[- ]titr|malentendant)\b/i.test(text)) detailHasSubtitles = true;
      if (/\b(ad|audiodescription)\b/i.test(text)) detailHasAudioDescription = true;
      if (/\b(hd|4k|uhd)\b/i.test(text)) detailIsHd = true;
      const m = text.match(/(-10|-12|-16|-18)/);
      if (m && !detailRating) detailRating = m[1];

      if (text.startsWith('Format :') && !format) {
        format = text.replace('Format :', '').trim();
      }
      if (text.startsWith('Genre :') && !genre) {
        genre = text.replace('Genre :', '').trim();
      }
      if ((text.startsWith('Sortie :') || text.startsWith('Année :')) && !year) {
        const matchY = text.match(/(\d{4})/);
        if (matchY) year = parseInt(matchY[1], 10);
      }
      if (text.startsWith('Pays :') && !country) {
        country = text.replace('Pays :', '').trim();
      }
      if (text.startsWith('De :') || text.startsWith('Réalisateur :') || text.startsWith('Réalisateurs :') || text.startsWith('Réalisation :')) {
        const dText = text.replace(/^(De|R[ée]alisateur|R[ée]alisateurs|R[ée]alisation)\s*:\s*/i, '').trim();
        if (dText) {
          const names = dText.split(/[,;&]|\bet\b/i).map(d => d.trim()).filter(d => d.length > 1);
          directors.push(...names);
        }
      }
    });

    if (!detailRating) {
      const csaAlt = $page('img[alt*="déconseillé"], img[src*="csa"], [class*="csa"]').attr('alt') || '';
      if (/moins de 10/i.test(csaAlt)) detailRating = '-10';
      else if (/moins de 12/i.test(csaAlt)) detailRating = '-12';
      else if (/moins de 16/i.test(csaAlt)) detailRating = '-16';
      else if (/moins de 18/i.test(csaAlt)) detailRating = '-18';
    }

    if (cleanLink.includes('/saisons/')) {
      const parentSeriesLink = cleanLink.replace(/\/saisons\/\d+.*$/, '/');
      try {
        const pRes = await axios.get(`https://www.programme-tv.net${parentSeriesLink}`, {
          headers: HTTP_HEADERS,
          timeout: 6000
        });
        const $pPage = cheerio.load(pRes.data);
        $pPage('.overview-stickerListItem').each((_, li) => {
          const text = $pPage(li).text().replace(/\s+/g, ' ').trim();
          if ((text.startsWith('Sortie :') || text.startsWith('Année :')) && !seriesYear) {
            const m = text.match(/(\d{4})/);
            if (m) seriesYear = parseInt(m[1], 10);
          }
          if (text.startsWith('Genre :') && !genre) {
            genre = text.replace('Genre :', '').trim();
          }
          if (text.startsWith('Format :') && !format) {
            format = text.replace('Format :', '').trim();
          }
          if (text.startsWith('De :') || text.startsWith('Réalisateur :') || text.startsWith('Réalisateurs :') || text.startsWith('Réalisation :')) {
            const dText = text.replace(/^(De|R[ée]alisateur|R[ée]alisateurs|R[ée]alisation)\s*:\s*/i, '').trim();
            if (dText) {
              const names = dText.split(/[,;&]|\bet\b/i).map(d => d.trim()).filter(d => d.length > 1);
              directors.push(...names);
            }
          }
        });

        let pSeriesSyn = $pPage('.synopsis-text').first().text().replace(/\s+/g, ' ').trim();
        if (!pSeriesSyn) {
          const synEl = $pPage('.synopsis, .program-summary').first().clone();
          synEl.find('button, .synopsis-action, a, [class*="action"], [class*="btn"]').remove();
          pSeriesSyn = synEl.text().replace(/\s+/g, ' ').replace(/^R[ée]sum[ée]\s+[^:]*:\s*/i, '').replace(/Voir plusmoins/gi, '').trim();
        }
        if (!pSeriesSyn) {
          const og = $pPage('meta[property="og:description"]').attr('content');
          if (og && og.length > 20 && !og.includes('Consultez la grille') && !og.endsWith('...') && !og.endsWith('…')) {
            pSeriesSyn = og.trim();
          }
        }
        if (pSeriesSyn && pSeriesSyn.length > 20 && !pSeriesSyn.endsWith('...') && !pSeriesSyn.endsWith('…')) {
          seriesDesc = pSeriesSyn;
        }
      } catch (parentPageErr) {
        if (process.env.DEBUG) console.error('Parent series fetch error:', parentPageErr.message);
      }
    }

    const uniqueDirectors = Array.from(new Set(directors));
    const result = { desc, seriesDesc, season, format, genre, year, seriesYear, country, episodeNumber, directors: uniqueDirectors, isUnreleased: detailIsUnreleased, isRerun: detailIsRerun, isLive: detailIsLive, hasSubtitles: detailHasSubtitles, hasAudioDescription: detailHasAudioDescription, isHd: detailIsHd, rating: detailRating };
    detailCache.set(link, result);
    return result;
  } catch (err) {
    if (process.env.DEBUG) console.error('Detail fetch error for ' + link + ':', err.message);
    detailCache.set(link, null);
    return null;
  }
}

async function fetchWithRetry(url, options, maxRetries = 2) {
  let attempt = 0;
  while (attempt <= maxRetries) {
    try {
      return await axios.get(url, options);
    } catch (err) {
      attempt++;
      if (attempt > maxRetries) throw err;
      await new Promise(r => setTimeout(r, 1000 * attempt));
    }
  }
}

async function parseHtmlSchedule(channel, html, baseDate, refData) {
  const $ = cheerio.load(html);
  const rawItems = $('.mainBroadcastCard, .gridBroadcastCard, [class*="broadcastCard"]');
  const extracted = [];

  let dayOffset = 0;
  let lastHour = -1;

  rawItems.each((_, el) => {
    const $el = $(el);
    const title = $el.find('.mainBroadcastCard-title, .gridBroadcastCard-title, h3, a[title]').first().text().trim();
    if (!title) return;

    const subTitle = $el.find('.mainBroadcastCard-subtitle, .gridBroadcastCard-subtitle, .subtitle').first().text().trim();
    const category = $el.find('.mainBroadcastCard-genre, .gridBroadcastCard-genre, .mainBroadcastCard-format, .genre').first().text().trim();
    const timeText = $el.find('.mainBroadcastCard-startingHour, .gridBroadcastCard-startingHour, time, .hour').first().text().trim();
    const imageSrc = extractImageSrc($el);
    const link = $el.find('a[href*="/programme/"], a[href*="/cinema/"], a[href*="/series/"]').first().attr('href');

    const stickers = [];
    $el.find('.mainBroadcastCard-sticker, .gridBroadcastCard-sticker, [class*="sticker"], [class*="badge"], [class*="c-badge"], .tag').each((_, s) => {
      const txt = $(s).text().trim();
      if (txt) stickers.push(txt);
    });

    const isRerun = $el.find('.mainBroadcastCard-rebroadcast').length > 0 || /rediff/i.test($el.text()) || stickers.some(s => /rediff/i.test(s));
    const isLive = $el.find('.mainBroadcastCard-live').length > 0 || stickers.some(s => /\bdirect\b/i.test(s));
    const isUnreleased = !isRerun && ($el.find('.mainBroadcastCard-new, .c-badge-new').length > 0 || stickers.some(s => /in[ée]dit/i.test(s)));
    const hasSubtitles = $el.find('.mainBroadcastCard-deaf, [class*="-deaf"], [aria-label*="Malentendant"], svg.icon-deaf').length > 0 || /deaf/i.test($el.html()) || stickers.some(s => /\b(st|sous[- ]titr|malentendant)\b/i.test(s));
    const hasAudioDescription = $el.find('.mainBroadcastCard-audioDescription, [class*="-audioDescription"], [aria-label*="Audio description"], svg.icon-audio-description').length > 0 || /audio-description|audiodescription/i.test($el.html()) || stickers.some(s => /\b(ad|audiodescription)\b/i.test(s));
    const isHd = stickers.some(s => /\bhd\b/i.test(s)) || /hd|4k|uhd/i.test($el.text());

    let cardRating = null;
    for (const s of stickers) {
      const m = s.match(/(-10|-12|-16|-18)/);
      if (m) { cardRating = m[1]; break; }
    }
    if (!cardRating) {
      const csaAlt = $el.find('img[alt*="déconseillé"], img[src*="csa"], [class*="csa"]').attr('alt') || '';
      if (/moins de 10/i.test(csaAlt)) cardRating = '-10';
      else if (/moins de 12/i.test(csaAlt)) cardRating = '-12';
      else if (/moins de 16/i.test(csaAlt)) cardRating = '-16';
      else if (/moins de 18/i.test(csaAlt)) cardRating = '-18';
    }

    const timeMatch = timeText.match(/(\d{1,2})[h:](\d{2})/i);
    if (!timeMatch) return;

    const hours = parseInt(timeMatch[1], 10);
    const minutes = parseInt(timeMatch[2], 10);
    if (lastHour >= 20 && hours < 6) {
      dayOffset = 1;
    }
    lastHour = hours;
    const startTime = baseDate.add(dayOffset, 'day').hour(hours).minute(minutes).second(0);

    extracted.push({
      title,
      subTitle: subTitle || null,
      category: category || 'General',
      startTime,
      imageSrc,
      link,
      hours,
      isUnreleased,
      isRerun,
      isLive,
      hasSubtitles,
      hasAudioDescription,
      isHd,
      rating: cardRating
    });
  });

  const programs = [];

  for (const item of extracted) {
    let matchedDesc = null;
    let matchedSubtitle = item.subTitle;
    let matchedCategory = item.category;
    let matchedRating = null;
    let matchedCredits = null;
    let matchedSeason = null;
    let matchedEpisode = null;
    let matchedYear = null;
    let matchedSeriesYear = null;
    let matchedCountry = null;

    const timeKey = `${channel.id}_${item.startTime.format('YYYYMMDDHHmm')}`;
    let refEntry = refData.timeMap.get(timeKey);
    if (refEntry && !areTitlesCompatible(refEntry.title, item.title)) {
      refEntry = null;
    }

    if (!refEntry) {
      const offsets = [5, -5, 10, -10, 15, -15];
      for (const offset of offsets) {
        const shiftedKey = `${channel.id}_${item.startTime.add(offset, 'minute').format('YYYYMMDDHHmm')}`;
        if (refData.timeMap.has(shiftedKey)) {
          const candidate = refData.timeMap.get(shiftedKey);
          if (areTitlesCompatible(candidate.title, item.title)) {
            refEntry = candidate;
            break;
          }
        }
      }
    }

    if (!refEntry && item.subTitle) {
      const daySubKey = `${channel.id}_${item.startTime.format('YYYYMMDD')}_${cleanTitle(item.title)}_${cleanTitle(item.subTitle)}`;
      refEntry = refData.titleMap.get(daySubKey);
    }

    let isDayFallback = false;
    if (!refEntry) {
      const dayKey = `${channel.id}_${item.startTime.format('YYYYMMDD')}_${cleanTitle(item.title)}`;
      refEntry = refData.titleMap.get(dayKey);
      if (refEntry) isDayFallback = true;
    }

    let isGeneralMatch = false;
    if (!refEntry) {
      const generalKey = `${channel.id}_${cleanTitle(item.title)}`;
      refEntry = refData.titleMap.get(generalKey);
      if (refEntry) isGeneralMatch = true;
    }

    if (refEntry) {
      if (!isGeneralMatch && !isDayFallback) {
        matchedDesc = refEntry.desc;
        if (!matchedSubtitle && refEntry.subTitle) matchedSubtitle = refEntry.subTitle;
        matchedSeason = refEntry.season;
        matchedEpisode = refEntry.episode;
      } else if (isDayFallback) {
        const cleanItemSub = cleanTitle(item.subTitle || '');
        const cleanRefSub = cleanTitle(refEntry.subTitle || '');
        if ((cleanItemSub && cleanRefSub && cleanItemSub === cleanRefSub) || (!cleanItemSub && !cleanRefSub)) {
          matchedDesc = refEntry.desc;
          if (!matchedSubtitle && refEntry.subTitle) matchedSubtitle = refEntry.subTitle;
          matchedSeason = refEntry.season;
          matchedEpisode = refEntry.episode;
        } else {
          if (refEntry.season != null && matchedSeason == null) matchedSeason = refEntry.season;
        }
      }
      if (item.category === 'General' && refEntry.category) matchedCategory = refEntry.category;
      matchedRating = refEntry.rating;
      matchedCredits = refEntry.credits;
      matchedYear = refEntry.year;
      matchedSeriesYear = refEntry.seriesYear;
      matchedCountry = refEntry.country;
    }

    let detailData = null;
    const isPrimeTime = item.hours >= 20 && item.hours <= 23;
    const isTruncated = !matchedDesc ||
      matchedDesc.endsWith('...') ||
      matchedDesc.endsWith('…') ||
      (!/[.!?\"»\)]$/.test(matchedDesc.trim()) && matchedDesc.length > 50) ||
      matchedDesc.includes('Toutes les infos de casting et de diffusions TV') ||
      matchedDesc.length < 180;
    const isSpecial = item.link && (item.link.includes('/cinema/') || item.link.includes('/series/') || item.link.includes('/saisons/'));
    if (item.link && (isPrimeTime || isTruncated || isSpecial)) {
      detailData = await fetchDetailPageData(item.link);
    }

    let primaryDesc = null;
    let secondaryDesc = null;

    const baseResolved = resolveDescriptions(matchedDesc, detailData ? detailData.desc : null);
    primaryDesc = baseResolved.primary;
    secondaryDesc = baseResolved.secondary;

    if (!secondaryDesc && detailData && detailData.seriesDesc) {
      const cleanSeries = sanitizeDescriptionText(detailData.seriesDesc);
      if (cleanSeries && cleanSeries.length > 20) {
        const normPrimary = normalizeForComparison(primaryDesc || '');
        const normSeries = normalizeForComparison(cleanSeries);
        if (normPrimary !== normSeries && !normPrimary.includes(normSeries) && !normSeries.includes(normPrimary)) {
          if (!primaryDesc) {
            primaryDesc = cleanSeries;
          } else {
            secondaryDesc = cleanSeries;
          }
        }
      }
    }

    const finalDirectors = (matchedCredits && matchedCredits.directors) ? [...matchedCredits.directors] : [];
    if (detailData && detailData.directors) {
      for (const d of detailData.directors) {
        if (!finalDirectors.includes(d)) finalDirectors.push(d);
      }
    }
    const finalCredits = {
      presenters: (matchedCredits && matchedCredits.presenters) || [],
      actors: (matchedCredits && matchedCredits.actors) || [],
      directors: finalDirectors
    };

    const isRerun = item.isRerun || (detailData && detailData.isRerun) || (refEntry && refEntry.isRerun) || false;
    const isUnreleased = !isRerun && ((detailData && detailData.isUnreleased) || item.isUnreleased || (refEntry && refEntry.isUnreleased) || false);
    const isLive = !isRerun && ((detailData && detailData.isLive) || item.isLive || false);
    const hasSubtitles = (detailData && detailData.hasSubtitles) || item.hasSubtitles || (refEntry && refEntry.hasSubtitles) || false;
    const hasAudioDescription = (detailData && detailData.hasAudioDescription) || item.hasAudioDescription || false;
    const isHd = (detailData && detailData.isHd) || item.isHd || (refEntry && refEntry.isHd) || false;
    const finalRating = (detailData && detailData.rating) || item.rating || matchedRating || null;

    let finalSeason = detailData && detailData.season != null ? detailData.season : matchedSeason;
    let finalEpisode = detailData && detailData.episodeNumber != null ? detailData.episodeNumber : matchedEpisode;

    const epMatch = (item.subTitle || matchedSubtitle || '').match(/(?:[ée]pisode|ep\.?)\s*(\d+)/i) || (item.title || '').match(/(?:[ée]pisode|ep\.?)\s*(\d+)/i);
    if (epMatch) {
      finalEpisode = parseInt(epMatch[1], 10);
    }
    const seasonMatch = (item.subTitle || matchedSubtitle || '').match(/saisons?\s*(\d+)/i) || (item.title || '').match(/saisons?\s*(\d+)/i);
    if (seasonMatch) {
      finalSeason = parseInt(seasonMatch[1], 10);
    }

    if (item.isUnreleased && finalSeason == null) {
      const latestSeason = refData.latestSeasonMap ? refData.latestSeasonMap.get(cleanTitle(item.title)) : null;
      if (latestSeason) {
        finalSeason = latestSeason;
      }
    }

    const isNewsOrWeather = (matchedCategory && /info|journal|m[ée]t[ée]o/i.test(matchedCategory)) && /\b(journal|m[ée]t[ée]o|jt|1945|1245)\b/i.test(item.title);
    if (isNewsOrWeather) {
      finalSeason = null;
      finalEpisode = null;
    }

    programs.push({
      channelId: channel.id,
      title: item.title,
      subTitle: matchedSubtitle,
      desc: primaryDesc,
      secondaryDesc: secondaryDesc,
      category: matchedCategory,
      format: detailData ? detailData.format : null,
      genre: detailData ? detailData.genre : null,
      year: detailData && detailData.year ? detailData.year : matchedYear,
      seriesYear: detailData && detailData.seriesYear ? detailData.seriesYear : matchedSeriesYear,
      country: detailData && detailData.country ? detailData.country : matchedCountry,
      season: finalSeason,
      episode: finalEpisode,
      start: item.startTime,
      stop: item.startTime.add(60, 'minute'),
      image: item.imageSrc,
      rating: finalRating,
      credits: finalCredits,
      isUnreleased,
      isRerun,
      isLive,
      hasSubtitles,
      hasAudioDescription,
      isHd
    });
  }

  for (let i = 0; i < programs.length - 1; i++) {
    programs[i].stop = programs[i + 1].start;

    const prev = programs[i];
    const curr = programs[i + 1];
    if (areTitlesCompatible(prev.title, curr.title) &&
        prev.season != null && curr.season != null && prev.season === curr.season &&
        prev.subTitle && curr.subTitle && cleanTitle(prev.subTitle) !== cleanTitle(curr.subTitle)) {
      if (prev.episode != null && (curr.episode == null || curr.episode <= prev.episode)) {
        curr.episode = prev.episode + 1;
      }
    }
  }

  return programs;
}

async function fetchChannelSchedule(channel, date, refData) {
  const formattedDate = date.format('YYYY-MM-DD');
  const dateKey8 = date.format('YYYYMMDD');
  const fallbackPrograms = refData.channelProgramsMap.get(`${channel.id}_${dateKey8}`) || [];

  const url = `https://www.programme-tv.net/programme/chaine/${formattedDate}/programme-${channel.siteId}.html`;
  try {
    const response = await fetchWithRetry(url, { headers: HTTP_HEADERS, timeout: 15000 });
    const programs = await parseHtmlSchedule(channel, response.data, date, refData);
    if (programs.length < 3 && fallbackPrograms.length > 0) {
      return fallbackPrograms;
    }
    return programs;
  } catch (err) {
    process.stderr.write(`Failed to fetch ${channel.id} for ${formattedDate}: ${err.message}. Using fallback.\n`);
    return fallbackPrograms;
  }
}

module.exports = {
  HTTP_HEADERS,
  loadXmltvReference,
  fetchDetailPageData,
  fetchWithRetry,
  fetchChannelSchedule,
  parseHtmlSchedule
};
