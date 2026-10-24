const dayjs = require('dayjs');
const utc = require('dayjs/plugin/utc');
const timezone = require('dayjs/plugin/timezone');

dayjs.extend(utc);
dayjs.extend(timezone);

const BOILERPLATE_PHRASES = [
  'Chaque jour, Laurent Mariotte propose des recettes simples',
  'Les prévisions météo pour la journée',
  'Les prévisions météo pour le lendemain',
  'Le bulletin météorologique international',
  'Un point quotidien sur l\'actualité météo',
  'Retrouvez tous vos programmes de nuit',
  'A suivre, des rediffusions des émissions de la chaîne',
  'Programmes de la nuit',
  'Fin des émissions',
  'Toutes les prévisions météo'
];

function sanitizeDescriptionText(text) {
  if (!text) return null;
  let cleaned = text.trim();
  if (cleaned.includes('<') && cleaned.includes('>')) {
    cleaned = cleaned.replace(/<br\s*\/?>/gi, '\n').replace(/<\/(?:p|div|li|h[1-6])>/gi, '\n').replace(/<[^>]+>/g, ' ');
  }
  if (cleaned.includes('&')) {
    cleaned = cleaned
      .replace(/&amp;/g, '&')
      .replace(/&quot;/g, '"')
      .replace(/&apos;|&#39;|&#039;/g, "'")
      .replace(/&lt;/g, '<')
      .replace(/&gt;/g, '>')
      .replace(/&nbsp;/g, ' ');
  }
  cleaned = cleaned.replace(/\r\n|\r/g, '\n').replace(/[ \t]+/g, ' ').replace(/\n{3,}/g, '\n\n').trim();
  for (const phrase of BOILERPLATE_PHRASES) {
    if (cleaned.toLowerCase().includes(phrase.toLowerCase())) {
      const reg = new RegExp('[^.\\n]*' + phrase.replace(/[.*+?^${}()|[\\]\\]/g, '\\$&') + '[^.\\n]*\\.?', 'gi');
      cleaned = cleaned.replace(reg, '').trim();
    }
  }
  cleaned = cleaned.replace(/[^.\n]*\b(?:télé[- ]loisirs|programme[- ]tv\.net)\b[^.\n]*\.?/gi, '').trim();
  cleaned = cleaned.replace(/\s{2,}/g, ' ').trim();
  if (cleaned.length < 15 || !/[a-zA-Z0-9]/.test(cleaned)) {
    return null;
  }
  return cleaned;
}

function normalizeForComparison(str) {
  return (str || '')
    .toLowerCase()
    .replace(/\u0153/g, 'oe')
    .replace(/\u00e6/g, 'ae')
    .replace(/[\u2019']/g, '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]/g, '');
}

function resolveDescriptions(source1, source2) {
  const clean1 = sanitizeDescriptionText(source1);
  const clean2 = sanitizeDescriptionText(source2);

  if (!clean1 && !clean2) return { primary: null, secondary: null };
  if (!clean1) return { primary: clean2, secondary: null };
  if (!clean2) return { primary: clean1, secondary: null };

  const norm1 = normalizeForComparison(clean1);
  const norm2 = normalizeForComparison(clean2);

  if (norm1 === norm2 || norm1.startsWith(norm2) || norm2.startsWith(norm1) || norm1.includes(norm2) || norm2.includes(norm1)) {
    const longer = clean1.length >= clean2.length ? clean1 : clean2;
    return { primary: longer, secondary: null };
  }

  return { primary: clean1, secondary: clean2 };
}

function cleanTitle(str) {
  return (str || '')
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]/g, '');
}

function areTitlesCompatible(titleA, titleB) {
  if (!titleA || !titleB) return false;
  const cleanA = cleanTitle(titleA);
  const cleanB = cleanTitle(titleB);
  if (!cleanA || !cleanB) return false;
  if (cleanA === cleanB) return true;
  if (cleanA.length >= 4 && cleanB.length >= 4) {
    if (cleanA.includes(cleanB) || cleanB.includes(cleanA)) return true;
  }
  const extractWords = (t) => (t || '')
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .split(/[^a-z0-9]+/)
    .filter(w => w.length >= 3);
  const wordsA = extractWords(titleA);
  const wordsB = extractWords(titleB);
  if (wordsA.length > 0 && wordsB.length > 0) {
    const common = wordsA.filter(w => wordsB.includes(w));
    if (common.length >= 2 || (wordsA.length === 1 && common.length === 1 && wordsB.length <= 2)) {
      return true;
    }
  }
  return false;
}

function escapeXml(unsafe) {
  if (!unsafe) return '';
  return unsafe
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;');
}

function formatXmltvDate(date) {
  return dayjs(date).tz('Europe/Paris').format('YYYYMMDDHHmmss ZZ');
}

function parseXmltvDate(dateStr) {
  if (!dateStr || dateStr.length < 14) return null;
  const raw = dateStr.trim();
  const year = parseInt(raw.substring(0, 4), 10);
  const month = parseInt(raw.substring(4, 6), 10) - 1;
  const day = parseInt(raw.substring(6, 8), 10);
  const hour = parseInt(raw.substring(8, 10), 10);
  const minute = parseInt(raw.substring(10, 12), 10);
  const second = parseInt(raw.substring(12, 14), 10);
  return dayjs().tz('Europe/Paris').year(year).month(month).date(day).hour(hour).minute(minute).second(second);
}

function extractImageSrc($el) {
  let chosen = '';
  const pictureSource = $el.find('picture source').first().attr('srcset');
  if (pictureSource) {
    chosen = pictureSource.split(',')[0].trim().split(' ')[0];
  }

  if (!chosen) {
    const imgEl = $el.find('img').first();
    if (imgEl.length) {
      const dataSrc = imgEl.attr('data-src');
      const dataSrcset = imgEl.attr('data-srcset') || imgEl.attr('srcset');
      const src = imgEl.attr('src');
      if (dataSrc && !dataSrc.startsWith('data:')) {
        chosen = dataSrc;
      } else if (dataSrcset) {
        chosen = dataSrcset.split(',')[0].trim().split(' ')[0];
      } else if (src && !src.startsWith('data:')) {
        chosen = src;
      }
    }
  }

  if (chosen.startsWith('//')) {
    chosen = `https:${chosen}`;
  }
  if (chosen.startsWith('data:')) {
    chosen = '';
  }
  if (chosen && chosen.includes('/64x90/')) {
    chosen = chosen.replace(/\/64x90\/quality\/\d+\//, '/640x360/quality/90/');
  }
  return chosen;
}

function resolvePrimaryParent(rawCategories, title = '', desc = '', season = null, episode = null) {
  const text = [title, ...rawCategories, (desc || '').substring(0, 120)].join(' ').toLowerCase();

  if (/\b(jt|journal|m[ée]t[ée]o|d[ée]bat|politique|[ée]mission politique|actualit[ée]|information|soir 3|12\.45|19\.45|le 20h|le 13h|culture infos|[ée]dition sp[ée]ciale|soci[ée]t[ée]|interview|en direct)\b/i.test(text) ||
      /magazine d'information|journal t[ée]l[ée]vis[ée]|flash info/i.test(text)) {
    return 'Info';
  }

  if (/\b(sport|football|foot|rugby|tennis|cyclisme|tour de france|basket|handball|volley|athl[ée]tisme|natation|boxe|mma|formule 1|f1|moto|automobile|hippisme|courses? hippiques?|golf|ski|omnisport)\b/i.test(text) ||
      /magazine sportif|championnat|coupe du monde|ligue 1|ligue des champions/i.test(text)) {
    return 'Sport';
  }

  if (/\b(jeunesse|animation|dessin anim[ée]|manga|anime|enfant|enfants|ado|junior|cartoon|pour enfants)\b/i.test(text) ||
      /s[ée]rie d'animation|programme jeunesse/i.test(text)) {
    return 'Jeunesse';
  }

  if (/\b(jeu|jeux|loterie|loto|tirage|euromillions|eurodreams|k[ée]no|quizz?|talk[- ]show|t[ée]l[ée]r[ée]alit[ée]|vari[ée]t[ée]|humour|spectacle|gala|culinaire|cuisine|gastronomie|t[ée]l[ée][- ]achat|mode|d[ée]co|relooking|sketch|stand[- ]up|cirque|magie|one[- ]man[- ]show|people|c[ée]r[ée]monie|divertissement)\b/i.test(text)) {
    return 'Divertissement';
  }

  if (/\b(t[ée]l[ée]film|film|court[- ]m[ée]trage|long[- ]m[ée]trage|cin[ée]ma|biopic|western)\b/i.test(text) ||
      /com[ée]die romantique|drame psychologique/i.test(text)) {
    return 'Film';
  }

  if (/\b(s[ée]rie|feuilleton|soap|sitcom|telenovela|t[ée]l[ée]novela|mini[- ]s[ée]rie|fiction|policier|drame|thriller|science[- ]fiction|fantastique)\b/i.test(text) ||
      /s[ée]rie tv|s[ée]rie dramatique|s[ée]rie polici[èe]re|com[ée]die dramatique/i.test(text) ||
      (season != null || episode != null)) {
    return 'Série';
  }

  if (/\b(documentaire|docu|d[ée]couverte|reportage|enqu[êe]te|animalier|animaux|histoire|civilisation|nature|sciences?|g[ée]opolitique|voyage|faits divers)\b/i.test(text) ||
      /docu[- ]fiction|grands reportages/i.test(text)) {
    return 'Documentaire';
  }

  if (/\b(musique|concert|clip|op[ée]ra|ballet|danse|chanson|pop|rock|jazz|live musical|festival musical)\b/i.test(text) ||
      /vari[ée]t[ée]s? musicales?/i.test(text)) {
    return 'Musique';
  }

  return 'Autre';
}

function sanitizeCategories(rawCategories, primaryParent) {
  const parentNames = ['Film', 'Série', 'Divertissement', 'Sport', 'Info', 'Documentaire', 'Jeunesse', 'Musique', 'Autre'];
  const cleaned = [];

  for (let cat of rawCategories) {
    if (!cat) continue;
    cat = cat.trim();
    if (!cat || cat.toLowerCase() === 'general' || cat.toLowerCase() === 'autre') continue;

    if (/jeunesses[ée]rie/i.test(cat)) {
      cat = "Série d'animation";
    }

    const matchesOtherParent = parentNames.some(p => p.toLowerCase() === cat.toLowerCase());
    if (matchesOtherParent) continue;

    if (primaryParent === 'Documentaire' && /^s[ée]rie( tv| dramatique)?$/i.test(cat)) continue;
    if (primaryParent === 'Film' && /^s[ée]rie$/i.test(cat)) continue;
    if (primaryParent === 'Jeunesse' && /^(sport|info|musique|s[ée]rie)$/i.test(cat)) continue;

    if (!cleaned.includes(cat)) {
      cleaned.push(cat);
    }
  }

  return [primaryParent, ...cleaned];
}

module.exports = {
  sanitizeDescriptionText,
  normalizeForComparison,
  resolveDescriptions,
  cleanTitle,
  areTitlesCompatible,
  escapeXml,
  formatXmltvDate,
  parseXmltvDate,
  extractImageSrc,
  resolvePrimaryParent,
  sanitizeCategories
};
