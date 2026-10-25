import os
import sys
import gzip
import json
import shutil
import unicodedata
import xml.etree.ElementTree as ET

os.environ.setdefault("ARGOS_BEAM_SIZE", "1")
os.environ.setdefault("ARGOS_INTER_THREADS", "2")

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
EXTRACTOR_DIR = os.path.dirname(SCRIPT_DIR)
OUTPUT_DIR = os.path.join(EXTRACTOR_DIR, "output")
CACHE_DIR = os.path.join(EXTRACTOR_DIR, "cache")
INPUT_XML = os.path.join(OUTPUT_DIR, "epg_tnt.xml")
OUTPUT_XML = os.path.join(OUTPUT_DIR, "epg_tnt_en.xml")
OUTPUT_GZ = os.path.join(OUTPUT_DIR, "epg_tnt_en.xml.gz")
CACHE_FILE = os.path.join(CACHE_DIR, "translation_cache.json")
APP_ASSET_DIR = os.path.abspath(os.path.join(EXTRACTOR_DIR, "../app/src/main/assets"))
APP_ASSET_GZ = os.path.join(APP_ASSET_DIR, "epg_tnt_en.xml.gz")

GENRE_MAP = {
    "cinema": "Cinema",
    "cinéma": "Cinema",
    "film": "Movie",
    "telefilm": "TV Movie",
    "téléfilm": "TV Movie",
    "telefilm d'animation": "Animated TV Movie",
    "téléfilm d'animation": "Animated TV Movie",
    "telefilm d'aventures": "Adventure TV Movie",
    "téléfilm d'aventures": "Adventure TV Movie",
    "telefilm dramatique": "Dramatic TV Movie",
    "téléfilm dramatique": "Dramatic TV Movie",
    "telefilm fantastique": "Fantasy TV Movie",
    "téléfilm fantastique": "Fantasy TV Movie",
    "telefilm feerique": "Fairytale TV Movie",
    "téléfilm féerique": "Fairytale TV Movie",
    "telefilm humoristique": "Comedy TV Movie",
    "téléfilm humoristique": "Comedy TV Movie",
    "telefilm policier": "Crime TV Movie",
    "téléfilm policier": "Crime TV Movie",
    "telefilm romanesque": "Romantic TV Movie",
    "téléfilm romanesque": "Romantic TV Movie",
    "telefilm sentimental": "Romantic TV Movie",
    "téléfilm sentimental": "Romantic TV Movie",
    "telefilm erotique": "Erotic TV Movie",
    "téléfilm érotique": "Erotic TV Movie",
    "court-metrage": "Short Film",
    "court-métrage": "Short Film",
    "court metrage": "Short Film",
    "court métrage": "Short Film",
    "court metrage d'animation": "Animated Short Film",
    "court métrage d'animation": "Animated Short Film",
    "court metrage dramatique": "Dramatic Short Film",
    "court métrage dramatique": "Dramatic Short Film",
    "moyen-metrage": "Medium-length Film",
    "moyen-métrage": "Medium-length Film",
    "film d'action": "Action Movie",
    "film d'animation": "Animated Film",
    "film d'aventures": "Adventure Movie",
    "film d'espionnage": "Spy Movie",
    "film d'horreur": "Horror Movie",
    "film de guerre": "War Movie",
    "film de science-fiction": "Sci-Fi Movie",
    "film de suspense": "Suspense Movie",
    "film documentaire": "Documentary Film",
    "film fantastique": "Fantasy Movie",
    "film policier": "Crime Movie",
    "film pour la jeunesse": "Children's Movie",
    "film sentimental": "Romantic Movie",
    "serie": "Series",
    "série": "Series",
    "serie tv": "TV Series",
    "série tv": "TV Series",
    "serie d'action": "Action Series",
    "série d'action": "Action Series",
    "serie d'animation": "Animated Series",
    "série d'animation": "Animated Series",
    "serie d'aventures": "Adventure Series",
    "série d'aventures": "Adventure Series",
    "serie de suspense": "Suspense Series",
    "série de suspense": "Suspense Series",
    "serie dramatique": "Drama Series",
    "série dramatique": "Drama Series",
    "serie fantastique": "Fantasy Series",
    "série fantastique": "Fantasy Series",
    "serie historique": "Historical Series",
    "série historique": "Historical Series",
    "serie hospitaliere": "Medical Drama Series",
    "série hospitalière": "Medical Drama Series",
    "serie humoristique": "Comedy Series",
    "série humoristique": "Comedy Series",
    "serie jeunesse": "Youth Series",
    "série jeunesse": "Youth Series",
    "serie policiere": "Crime Series",
    "série policière": "Crime Series",
    "serie realiste": "Realistic Drama Series",
    "série réaliste": "Realistic Drama Series",
    "serie sentimentale": "Romantic Series",
    "série sentimentale": "Romantic Series",
    "mini-serie": "Mini-series",
    "mini-série": "Mini-series",
    "feuilleton": "Drama Series",
    "feuilleton policier": "Crime Drama Series",
    "feuilleton realiste": "Realistic Soap Opera",
    "feuilleton réaliste": "Realistic Soap Opera",
    "feuilleton sentimental": "Romantic Soap Opera",
    "documentaire": "Documentary",
    "docu-fiction": "Docudrama",
    "docufiction": "Docudrama",
    "magazine": "Magazine",
    "magazine animalier": "Wildlife Magazine",
    "magazine culinaire": "Culinary Magazine",
    "magazine culturel": "Cultural Magazine",
    "magazine d'actualite": "Current Affairs Magazine",
    "magazine d'actualité": "Current Affairs Magazine",
    "magazine d'information": "News Magazine",
    "magazine de decouvertes": "Discovery Magazine",
    "magazine de découvertes": "Discovery Magazine",
    "magazine de geopolitique": "Geopolitics Magazine",
    "magazine de géopolitique": "Geopolitics Magazine",
    "magazine de l'art de vivre": "Lifestyle Magazine",
    "magazine de l'automobile": "Automotive Magazine",
    "magazine de l'environnement": "Environmental Magazine",
    "magazine de l'economie": "Business Magazine",
    "magazine de l'économie": "Business Magazine",
    "magazine de la gastronomie": "Gastronomy Magazine",
    "magazine de la mer": "Marine Magazine",
    "magazine de la mode": "Fashion Magazine",
    "magazine de la nature": "Nature Magazine",
    "magazine de la sante": "Health Magazine",
    "magazine de la santé": "Health Magazine",
    "magazine de reportages": "Investigative Magazine",
    "magazine de services": "Consumer Services Magazine",
    "magazine de societe": "Society Magazine",
    "magazine de société": "Society Magazine",
    "magazine de tele-achat": "Home Shopping Magazine",
    "magazine de télé-achat": "Home Shopping Magazine",
    "magazine des beaux-arts": "Fine Arts Magazine",
    "magazine des medias": "Media Magazine",
    "magazine des médias": "Media Magazine",
    "magazine du cinema": "Cinema Magazine",
    "magazine du cinéma": "Cinema Magazine",
    "magazine du consommateur": "Consumer Affairs Magazine",
    "magazine du jardinage": "Gardening Magazine",
    "magazine du multimedia": "Tech & Multimedia Magazine",
    "magazine du multimédia": "Tech & Multimedia Magazine",
    "magazine du show-biz": "Showbiz Magazine",
    "magazine du tourisme": "Tourism Magazine",
    "magazine historique": "History Magazine",
    "magazine jeunesse": "Youth Magazine",
    "magazine judiciaire": "Legal Magazine",
    "magazine litteraire": "Literary Magazine",
    "magazine littéraire": "Literary Magazine",
    "magazine musical": "Music Magazine",
    "magazine politique": "Political Magazine",
    "magazine religieux": "Religious Magazine",
    "magazine regional": "Regional Magazine",
    "magazine régional": "Regional Magazine",
    "magazine scientifique": "Science Magazine",
    "magazine sportif": "Sports Magazine",
    "magazine educatif": "Educational Magazine",
    "magazine éducatif": "Educational Magazine",
    "mag culture": "Culture Magazine",
    "mag sciences": "Science Magazine",
    "mag societe": "Society Magazine",
    "mag société": "Society Magazine",
    "piece de theatre": "Theater Play",
    "pièce de théâtre": "Theater Play",
    "theatre": "Theater",
    "théâtre": "Theater",
    "vaudeville": "Vaudeville",
    "spectacle": "Show",
    "one man show": "Stand-up Comedy",
    "one-man show": "Comedy Show",
    "one-woman show": "Comedy Show",
    "humour": "Comedy",
    "comedie": "Comedy",
    "comédie": "Comedy",
    "comedie dramatique": "Comedy Drama",
    "comédie dramatique": "Comedy Drama",
    "comedie musicale": "Musical Comedy",
    "comédie musicale": "Musical Comedy",
    "comedie policiere": "Crime Comedy",
    "comédie policière": "Crime Comedy",
    "comedie sentimentale": "Romantic Comedy",
    "comédie sentimentale": "Romantic Comedy",
    "drame": "Drama",
    "drame lyrique": "Lyric Drama",
    "melodrame": "Melodrama",
    "mélodrame": "Melodrama",
    "policier": "Crime / Detective",
    "thriller": "Thriller",
    "thriller politique": "Political Thriller",
    "suspense": "Suspense",
    "action": "Action",
    "aventures": "Adventure",
    "aventure": "Adventure",
    "western": "Western",
    "guerre": "War",
    "science-fiction": "Sci-Fi",
    "fantastique": "Fantasy",
    "horreur": "Horror",
    "biopic": "Biopic",
    "biographie": "Biography",
    "romance": "Romance",
    "fiction": "Fiction",
    "journal": "News",
    "journal televise": "TV News",
    "journal télévisé": "TV News",
    "info": "News",
    "infos": "News",
    "information": "News & Current Affairs",
    "jt sport": "Sports News",
    "interview": "Interview",
    "debat": "Debate",
    "débat": "Debate",
    "debat parlementaire": "Parliamentary Debate",
    "débat parlementaire": "Parliamentary Debate",
    "talk-show": "Talk Show",
    "talk show": "Talk Show",
    "divertissement": "Entertainment",
    "divertissement-humour": "Comedy Entertainment",
    "varietes": "Variety",
    "variétés": "Variety",
    "jeu": "Game Show",
    "jeux": "Game Shows",
    "telerealite": "Reality TV",
    "télérealité": "Reality TV",
    "télé-réalité": "Reality TV",
    "jeunesse": "Kids",
    "enfants": "Kids",
    "emission jeunesse": "Kids Show",
    "émission jeunesse": "Kids Show",
    "dessin anime": "Cartoon",
    "dessin animé": "Cartoon",
    "dessin anime manga": "Manga Animation",
    "dessin animé manga": "Manga Animation",
    "animation": "Animation",
    "manga": "Anime",
    "anime": "Anime",
    "musique": "Music",
    "musique classique": "Classical Music",
    "classique": "Classical",
    "musical": "Musical",
    "concert": "Concert",
    "opera": "Opera",
    "opéra": "Opera",
    "clips": "Music Videos",
    "emission musicale": "Music Show",
    "émission musicale": "Music Show",
    "pop & rock": "Pop & Rock",
    "rap": "Rap & Hip-Hop",
    "sport": "Sports",
    "sports": "Sports",
    "multisports": "Multi-sports",
    "sports mecaniques": "Motorsports",
    "sports mécaniques": "Motorsports",
    "endurance": "Endurance Racing",
    "football": "Soccer / Football",
    "football americain": "American Football",
    "football américain": "American Football",
    "rugby": "Rugby",
    "basketball": "Basketball",
    "basket-ball": "Basketball",
    "tennis": "Tennis",
    "tennis de table": "Table Tennis",
    "cyclisme": "Cycling",
    "formule 1": "Formula 1",
    "badminton": "Badminton",
    "patinage artistique": "Figure Skating",
    "triathlon": "Triathlon",
    "marathon": "Marathon",
    "mma": "MMA",
    "surf": "Surfing",
    "voile": "Sailing",
    "poker": "Poker",
    "peche": "Fishing",
    "pêche": "Fishing",
    "hippisme": "Horse Racing",
    "culture infos": "Culture & News",
    "culture": "Culture",
    "civilisations": "Civilizations",
    "art de vivre": "Lifestyle",
    "beaux-arts": "Fine Arts",
    "histoire": "History",
    "lettres": "Literature",
    "voyage": "Travel",
    "nature": "Nature",
    "environnement": "Environment",
    "animalier": "Wildlife",
    "animaux": "Wildlife",
    "science": "Science",
    "sciences": "Science",
    "sciences et technique": "Science & Tech",
    "societe": "Society",
    "société": "Society",
    "sante": "Health",
    "santé": "Health",
    "education": "Education",
    "éducation": "Education",
    "geopolitique": "Geopolitics",
    "géopolitique": "Geopolitics",
    "politique": "Politics",
    "emission politique": "Political Show",
    "émission politique": "Political Show",
    "emission speciale": "Special Broadcast",
    "émission spéciale": "Special Broadcast",
    "justice": "Justice & Law",
    "religions": "Religion",
    "ceremonie": "Ceremony",
    "cérémonie": "Ceremony",
    "decouvertes": "Discoveries",
    "découvertes": "Discoveries",
    "cuisine": "Cooking",
    "gastronomie": "Gastronomy",
    "meteo": "Weather",
    "météo": "Weather",
    "loterie": "Lottery",
    "tirage": "Lottery",
    "direct": "Live",
    "rediffusion": "Rerun",
    "audiodescription": "Audio Description",
    "cirque": "Circus",
    "programme indetermine": "TBA",
    "programme indéterminé": "TBA",
    "autre": "Other"
}

COUNTRY_MAP = {
    "france": "France",
    "états-unis": "United States",
    "etats-unis": "United States",
    "royaume-uni": "United Kingdom",
    "allemagne": "Germany",
    "italie": "Italy",
    "espagne": "Spain",
    "belgique": "Belgium",
    "canada": "Canada",
    "suisse": "Switzerland",
    "japon": "Japan",
    "australie": "Australia",
    "chine": "China",
    "inde": "India"
}

def load_cache():
    if os.path.exists(CACHE_FILE):
        try:
            with open(CACHE_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception as e:
            sys.stderr.write(f"Cache load error: {e}\n")
    return {}

def save_cache(cache):
    os.makedirs(CACHE_DIR, exist_ok=True)
    with open(CACHE_FILE, "w", encoding="utf-8") as f:
        json.dump(cache, f, ensure_ascii=False, indent=2)

def init_argos():
    try:
        import argostranslate.package
        import argostranslate.translate

        installed_languages = argostranslate.translate.get_installed_languages()
        from_lang = next((l for l in installed_languages if l.code == "fr"), None)
        to_lang = next((l for l in installed_languages if l.code == "en"), None)

        if not from_lang or not to_lang or not from_lang.get_translation(to_lang):
            sys.stdout.write("Downloading Argos Translate French-to-English package...\n")
            argostranslate.package.update_package_index()
            available_packages = argostranslate.package.get_available_packages()
            pkg = next((p for p in available_packages if p.from_code == "fr" and p.to_code == "en"), None)
            if pkg:
                download_path = pkg.download()
                argostranslate.package.install_from_path(download_path)
                installed_languages = argostranslate.translate.get_installed_languages()
                from_lang = next((l for l in installed_languages if l.code == "fr"), None)
                to_lang = next((l for l in installed_languages if l.code == "en"), None)

        if from_lang and to_lang:
            return from_lang.get_translation(to_lang)
    except Exception as e:
        sys.stderr.write(f"Argos Translate initialization warning: {e}\n")
    return None

def strip_accents(s):
    return unicodedata.normalize("NFD", s).encode("ascii", "ignore").decode("utf-8")

def translate_genre(text, cache=None, translator=None):
    if not text:
        return text
    clean = text.strip().replace("&apos;", "'").replace("&amp;", "&")
    lower = clean.lower()
    if lower in GENRE_MAP:
        return GENRE_MAP[lower]
    unaccented = strip_accents(lower)
    if unaccented in GENRE_MAP:
        return GENRE_MAP[unaccented]
    for prefix, rep in [
        ("film : ", "Movie: "),
        ("film - ", "Movie: "),
        ("série : ", "Series: "),
        ("serie : ", "Series: "),
        ("série - ", "Series: "),
        ("documentaire : ", "Documentary: "),
        ("documentaire - ", "Documentary: "),
        ("magazine : ", "Magazine: "),
        ("magazine - ", "Magazine: ")
    ]:
        prefix_unacc = strip_accents(prefix)
        if lower.startswith(prefix):
            suffix = clean[len(prefix):].strip()
            sub = translate_genre(suffix, cache, translator)
            return rep + sub
        if unaccented.startswith(prefix_unacc):
            suffix = clean[len(prefix_unacc):].strip()
            sub = translate_genre(suffix, cache, translator)
            return rep + sub
    if cache and clean in cache:
        return cache[clean]
    if cache and lower in cache:
        return cache[lower]
    if cache and unaccented in cache:
        return cache[unaccented]
    return clean

def translate_country(text):
    if not text:
        return text
    clean = text.strip()
    return COUNTRY_MAP.get(clean.lower(), clean)

def main():
    if not os.path.exists(INPUT_XML):
        sys.stderr.write(f"Input file not found: {INPUT_XML}\n")
        sys.exit(1)

    sys.stdout.write(f"Reading {INPUT_XML}...\n")
    tree = ET.parse(INPUT_XML)
    root = tree.getroot()

    cache = load_cache()
    translator = init_argos()

    unique_texts = set()
    for prog in root.findall("programme"):
        title_elem = prog.find("title")
        if title_elem is not None and title_elem.text:
            unique_texts.add(title_elem.text.strip())
        sub_title_elem = prog.find("sub-title")
        if sub_title_elem is not None and sub_title_elem.text:
            unique_texts.add(sub_title_elem.text.strip())
        for desc_elem in prog.findall("desc"):
            if desc_elem is not None and desc_elem.text:
                unique_texts.add(desc_elem.text.strip())
        desc_more_elem = prog.find("desc-more")
        if desc_more_elem is not None and desc_more_elem.text:
            unique_texts.add(desc_more_elem.text.strip())
        for cat_elem in prog.findall("category"):
            if cat_elem is not None and cat_elem.text:
                clean_cat = cat_elem.text.strip().replace("&apos;", "'").replace("&amp;", "&")
                if clean_cat.lower() not in GENRE_MAP:
                    unique_texts.add(clean_cat)

    missing = [t for t in unique_texts if t not in cache]
    sys.stdout.write(f"Total unique texts: {len(unique_texts)}, cached: {len(unique_texts) - len(missing)}, to translate: {len(missing)}\n")

    if missing and translator:
        batch_size = 50
        for i, text in enumerate(missing):
            try:
                translated = translator.translate(text)
                cache[text] = translated if translated else text
            except Exception as e:
                cache[text] = text
            if (i + 1) % batch_size == 0 or (i + 1) == len(missing):
                sys.stdout.write(f"Translated {i + 1}/{len(missing)} items...\n")
                sys.stdout.flush()
                save_cache(cache)
        save_cache(cache)
    elif missing and not translator:
        sys.stdout.write("Argos Translate not available, preserving original text for uncached entries.\n")
        sys.stdout.flush()

    for prog in root.findall("programme"):
        title_elem = prog.find("title")
        if title_elem is not None and title_elem.text:
            title_elem.text = cache.get(title_elem.text.strip(), title_elem.text)
            title_elem.set("lang", "en")

        sub_title_elem = prog.find("sub-title")
        if sub_title_elem is not None and sub_title_elem.text:
            sub_title_elem.text = cache.get(sub_title_elem.text.strip(), sub_title_elem.text)
            sub_title_elem.set("lang", "en")

        for desc_elem in prog.findall("desc"):
            if desc_elem is not None and desc_elem.text:
                desc_elem.text = cache.get(desc_elem.text.strip(), desc_elem.text)
                current_lang = desc_elem.get("lang")
                if current_lang == "fr-more" or current_lang == "more":
                    desc_elem.set("lang", "en-more")
                else:
                    desc_elem.set("lang", "en")

        desc_more_elem = prog.find("desc-more")
        if desc_more_elem is not None and desc_more_elem.text:
            desc_more_elem.text = cache.get(desc_more_elem.text.strip(), desc_more_elem.text)
            desc_more_elem.set("lang", "en-more")

        for cat_elem in prog.findall("category"):
            if cat_elem is not None and cat_elem.text:
                cat_elem.text = translate_genre(cat_elem.text, cache, translator)
                cat_elem.set("lang", "en")

        country_elem = prog.find("country")
        if country_elem is not None and country_elem.text:
            country_elem.text = translate_country(country_elem.text)
            country_elem.set("lang", "en")

    os.makedirs(OUTPUT_DIR, exist_ok=True)
    xml_header = '<?xml version="1.0" encoding="UTF-8"?>\n<!DOCTYPE tv SYSTEM "xmltv.dtd">\n'
    raw_xml = ET.tostring(root, encoding="utf-8")

    with open(OUTPUT_XML, "wb") as f:
        f.write(xml_header.encode("utf-8"))
        f.write(raw_xml)

    with gzip.open(OUTPUT_GZ, "wb", compresslevel=9) as f:
        f.write(xml_header.encode("utf-8"))
        f.write(raw_xml)

    if os.path.exists(APP_ASSET_DIR):
        shutil.copyfile(OUTPUT_GZ, APP_ASSET_GZ)

    sys.stdout.write(f"Successfully generated English EPG in {OUTPUT_XML} and {OUTPUT_GZ}\n")

if __name__ == "__main__":
    main()
