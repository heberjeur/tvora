const fs = require('fs');
const path = require('path');
const zlib = require('zlib');
const dayjs = require('dayjs');
const utc = require('dayjs/plugin/utc');
const timezone = require('dayjs/plugin/timezone');

dayjs.extend(utc);
dayjs.extend(timezone);

const CHANNELS = require('./channels.json');
const {
  escapeXml,
  formatXmltvDate,
  resolvePrimaryParent,
  sanitizeCategories
} = require('./normalizer');
const {
  loadXmltvReference,
  fetchChannelSchedule
} = require('./fetcher');

const OUTPUT_DIR = path.resolve(__dirname, '../output');
const OUTPUT_XML = path.join(OUTPUT_DIR, 'epg_tnt.xml');
const OUTPUT_GZ = path.join(OUTPUT_DIR, 'epg_tnt.xml.gz');
const DAYS_TO_FETCH = 11;

function buildXml(channels, programs) {
  let xml = '<?xml version="1.0" encoding="UTF-8"?>\n';
  xml += '<!DOCTYPE tv SYSTEM "xmltv.dtd">\n';
  xml += '<tv source-info-name="TVora TNT+ Extractor" source-info-url="https://github.com/heberjeur/tv-guide">\n';

  for (const channel of channels) {
    xml += `  <channel id="${escapeXml(channel.id)}">\n`;
    xml += `    <display-name>${escapeXml(channel.name)}</display-name>\n`;
    if (channel.icon) {
      xml += `    <icon src="${escapeXml(channel.icon)}" />\n`;
    }
    xml += `  </channel>\n`;
  }

  for (const program of programs) {
    const startStr = formatXmltvDate(program.start);
    const stopStr = formatXmltvDate(program.stop);
    xml += `  <programme start="${startStr}" stop="${stopStr}" channel="${escapeXml(program.channelId)}">\n`;
    xml += `    <title lang="fr">${escapeXml(program.title)}</title>\n`;
    if (program.subTitle) {
      xml += `    <sub-title lang="fr">${escapeXml(program.subTitle)}</sub-title>\n`;
    }
    if (program.desc) {
      xml += `    <desc lang="fr">${escapeXml(program.desc)}</desc>\n`;
    }
    if (program.secondaryDesc) {
      xml += `    <desc-more lang="fr">${escapeXml(program.secondaryDesc)}</desc-more>\n`;
      xml += `    <desc lang="fr-more">${escapeXml(program.secondaryDesc)}</desc>\n`;
    }
    if (program.year) {
      xml += `    <date>${escapeXml(String(program.year))}</date>\n`;
    }
    if (program.seriesYear) {
      xml += `    <orig-date>${escapeXml(String(program.seriesYear))}</orig-date>\n`;
    }
    if (program.country) {
      xml += `    <country lang="fr">${escapeXml(program.country)}</country>\n`;
    }
    if (program.season != null || program.episode != null) {
      const s = program.season != null ? (program.season - 1) : '';
      const e = program.episode != null ? (program.episode - 1) : '';
      xml += `    <episode-num system="xmltv_ns">${s}.${e}.</episode-num>\n`;
    }
    const rawCategories = [];
    if (program.category && program.category !== 'General') rawCategories.push(program.category);
    if (program.format && !rawCategories.includes(program.format)) rawCategories.push(program.format);
    if (program.genre && !rawCategories.includes(program.genre)) rawCategories.push(program.genre);
    if (program.isRerun && !rawCategories.includes('Rediffusion')) rawCategories.push('Rediffusion');
    if (program.isLive && !rawCategories.includes('Direct')) rawCategories.push('Direct');
    if (program.hasAudioDescription && !rawCategories.includes('Audiodescription')) rawCategories.push('Audiodescription');

    const primaryParent = resolvePrimaryParent(rawCategories, program.title || '', program.desc || '', program.season, program.episode);
    const categories = sanitizeCategories(rawCategories, primaryParent);

    for (const cat of categories) {
      xml += `    <category lang="fr">${escapeXml(cat)}</category>\n`;
    }
    if (program.image) {
      xml += `    <icon src="${escapeXml(program.image)}" />\n`;
    }
    if (program.isUnreleased) {
      xml += `    <premiere />\n`;
    }
    if (program.isRerun) {
      xml += `    <previously-shown />\n`;
    }
    if (program.hasSubtitles) {
      xml += `    <subtitles type="teletext" />\n`;
    }
    if (program.hasAudioDescription) {
      xml += `    <audio-description />\n`;
    }
    if (program.isHd) {
      xml += `    <video>\n      <quality>HD</quality>\n    </video>\n`;
    }
    if (program.rating) {
      xml += `    <rating system="CSA">\n`;
      xml += `      <value>${escapeXml(program.rating)}</value>\n`;
      xml += `    </rating>\n`;
    }
    if (program.credits) {
      const { presenters, actors, directors } = program.credits;
      const hasCredits = (presenters && presenters.length) || (actors && actors.length) || (directors && directors.length);
      if (hasCredits) {
        xml += `    <credits>\n`;
        if (directors) {
          for (const d of directors) xml += `      <director>${escapeXml(d)}</director>\n`;
        }
        if (actors) {
          for (const a of actors) xml += `      <actor>${escapeXml(a)}</actor>\n`;
        }
        if (presenters) {
          for (const p of presenters) xml += `      <presenter>${escapeXml(p)}</presenter>\n`;
        }
        xml += `    </credits>\n`;
      }
    }
    xml += `  </programme>\n`;
  }

  xml += '</tv>\n';
  return xml;
}

async function main() {
  if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
  }

  process.stdout.write('Loading reference EPG data...\n');
  const refData = await loadXmltvReference(CHANNELS);
  process.stdout.write(`Indexed ${refData.timeMap.size} reference descriptions and ${refData.channelProgramsMap.size} channel-day blocks.\n`);

  const today = dayjs().tz('Europe/Paris');
  const allPrograms = [];
  const batchSize = 4;

  for (let i = 0; i < CHANNELS.length; i += batchSize) {
    const batch = CHANNELS.slice(i, i + batchSize);
    const batchResults = await Promise.all(
      batch.map(async (channel) => {
        const channelPrograms = [];
        for (let dayOffset = -1; dayOffset < DAYS_TO_FETCH; dayOffset++) {
          const targetDate = today.add(dayOffset, 'day');
          const programs = await fetchChannelSchedule(channel, targetDate, refData);
          channelPrograms.push(...programs);
        }
        channelPrograms.sort((a, b) => a.start.valueOf() - b.start.valueOf());
        const deduped = [];
        for (const prog of channelPrograms) {
          if (deduped.length === 0) {
            deduped.push(prog);
            continue;
          }
          const prev = deduped[deduped.length - 1];
          if (Math.abs(prog.start.diff(prev.start, 'minute')) < 5) {
            if ((prog.desc || '').length > (prev.desc || '').length) {
              deduped[deduped.length - 1] = prog;
            }
            continue;
          }
          if (prog.start.isBefore(prev.start)) {
            continue;
          }
          deduped.push(prog);
        }
        for (let j = 0; j < deduped.length - 1; j++) {
          deduped[j].stop = deduped[j + 1].start;
        }
        return deduped;
      })
    );
    for (const res of batchResults) {
      allPrograms.push(...res);
    }
  }

  const xmlContent = buildXml(CHANNELS, allPrograms);
  fs.writeFileSync(OUTPUT_XML, xmlContent, 'utf-8');

  const gzippedContent = zlib.gzipSync(Buffer.from(xmlContent, 'utf-8'), { level: 9 });
  fs.writeFileSync(OUTPUT_GZ, gzippedContent);

  const assetGz = path.resolve(__dirname, '../../app/src/main/assets/epg_tnt.xml.gz');
  if (fs.existsSync(path.dirname(assetGz))) {
    fs.writeFileSync(assetGz, gzippedContent);
  }

  process.stdout.write(`Successfully generated ${allPrograms.length} TNT+ programs in ${OUTPUT_XML} and ${OUTPUT_GZ}\n`);
}

main().catch((err) => {
  process.stderr.write(`${err.message}\n`);
  process.exit(1);
});
