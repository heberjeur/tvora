import os
import sys
import gzip
import json
import shutil
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
    "court-metrage": "Short Film",
    "court-métrage": "Short Film",
    "moyen-metrage": "Medium-length Film",
    "moyen-métrage": "Medium-length Film",
    "serie": "Series",
    "série": "Series",
    "serie tv": "TV Series",
    "série tv": "TV Series",
    "mini-serie": "Mini-series",
    "mini-série": "Mini-series",
    "feuilleton": "Drama Series",
    "documentaire": "Documentary",
    "docu-fiction": "Docudrama",
    "docufiction": "Docudrama",
    "magazine": "Magazine",
    "journal": "News",
    "journal televise": "TV News",
    "journal télévisé": "TV News",
    "divertissement": "Entertainment",
    "jeu": "Game Show",
    "jeux": "Game Shows",
    "telerealite": "Reality TV",
    "télérealité": "Reality TV",
    "télé-réalité": "Reality TV",
    "jeunesse": "Kids",
    "enfants": "Kids",
    "dessin anime": "Cartoon",
    "dessin animé": "Cartoon",
    "animation": "Animation",
    "manga": "Anime",
    "anime": "Anime",
    "musique": "Music",
    "musical": "Musical",
    "concert": "Concert",
    "opera": "Opera",
    "opéra": "Opera",
    "spectacle": "Show",
    "theatre": "Theater",
    "théâtre": "Theater",
    "humour": "Comedy",
    "one-man show": "Comedy Show",
    "one-woman show": "Comedy Show",
    "sport": "Sports",
    "sports": "Sports",
    "culture infos": "Culture & News",
    "culture": "Culture",
    "info": "News",
    "information": "News & Current Affairs",
    "debat": "Debate",
    "débat": "Debate",
    "talk-show": "Talk Show",
    "talk show": "Talk Show",
    "meteo": "Weather",
    "météo": "Weather",
    "loterie": "Lottery",
    "tirage": "Lottery",
    "autre": "Other",
    "action": "Action",
    "comedie": "Comedy",
    "comédie": "Comedy",
    "drame": "Drama",
    "policier": "Crime / Detective",
    "thriller": "Thriller",
    "suspense": "Suspense",
    "science-fiction": "Sci-Fi",
    "fantastique": "Fantasy",
    "horreur": "Horror",
    "aventure": "Adventure",
    "western": "Western",
    "guerre": "War",
    "biopic": "Biopic",
    "romance": "Romance",
    "nature": "Nature",
    "animaux": "Wildlife",
    "science": "Science",
    "sciences": "Science",
    "societe": "Society",
    "société": "Society",
    "histoire": "History",
    "voyage": "Travel",
    "cuisine": "Cooking",
    "gastronomie": "Gastronomy",
    "football": "Soccer / Football",
    "rugby": "Rugby",
    "basketball": "Basketball",
    "tennis": "Tennis",
    "cyclisme": "Cycling",
    "formule 1": "Formula 1"
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

def translate_genre(text):
    if not text:
        return text
    clean = text.strip()
    lower = clean.lower()
    if lower in GENRE_MAP:
        return GENRE_MAP[lower]
    for prefix, rep in [("film : ", "Movie: "), ("série : ", "Series: "), ("documentaire : ", "Documentary: ")]:
        if lower.startswith(prefix):
            suffix = clean[len(prefix):].strip()
            sub = translate_genre(suffix)
            return rep + sub
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
                cat_elem.text = translate_genre(cat_elem.text)
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
