"""AI 팁 묶음 형식 확인. 앱(AiTips.parse)과 같은 규칙이다. 하나라도 어긋나면 실패한다."""
import json, re, sys, datetime
from urllib.parse import urlparse

KINDS = {"rule", "tone", "example", "tip"}
ROLES = {"developer", "designer", "planner", "all"}
BASES = {"official", "github", "community"}

def text(node, field, limit, required):
    value = str(node.get(field, "") or "").strip()
    if (required and not value) or len(value) > limit or any(ord(ch) < 0x20 and ch != "\n" for ch in value):
        raise ValueError(f"{field}: 비었거나 너무 길어요")
    return value

def date(node, field):
    value = text(node, field, 10, False)
    if value:
        datetime.date.fromisoformat(value)

def check(path):
    raw = open(path, "rb").read()
    if len(raw) > 1024 * 1024:
        raise ValueError("1MB를 넘어요")
    root = json.loads(raw)
    if root.get("format") != 1:
        raise ValueError("format은 1이어야 해요")
    if not re.fullmatch(r"\d{4}-\d{2}-\d{2}\.\d{1,4}", text(root, "version", 40, True)):
        raise ValueError("version은 날짜.번호 모양이어야 해요")
    date(root, "publishedAt")
    cards, ids = root.get("cards", []), set()
    if len(cards) > 500:
        raise ValueError("카드는 500개까지예요")
    for c in cards:
        cid = text(c, "id", 80, True)
        if not re.fullmatch(r"[a-z0-9][a-z0-9-]{0,79}", cid) or cid in ids:
            raise ValueError(f"id가 잘못됐거나 겹쳐요: {cid}")
        ids.add(cid)
        if text(c, "kind", 10, True) not in KINDS:
            raise ValueError(f"{cid}: kind")
        roles = c.get("roles") or []
        if not roles or any(r not in ROLES for r in roles):
            raise ValueError(f"{cid}: roles")
        text(c, "topic", 40, False); text(c, "title", 80, True); text(c, "text", 400, True); text(c, "why", 300, False)
        source = c.get("source") or {}
        text(source, "title", 120, True)
        url = urlparse(text(source, "url", 500, True))
        if url.scheme != "https" or not url.hostname or url.username or url.password:
            raise ValueError(f"{cid}: 출처 주소는 https여야 해요")
        date(c, "checkedAt"); date(c, "addedAt")
        pop = c.get("popularity")
        if pop:
            if text(pop, "basis", 20, False) not in BASES:
                raise ValueError(f"{cid}: popularity.basis")
            text(pop, "note", 60, False)
        for field in ("title", "text", "why"):
            if "·" in str(c.get(field, "")):
                raise ValueError(f"{cid}: 가운뎃점은 쓰지 않아요")
    return len(cards)

if __name__ == "__main__":
    try:
        print(f"형식이 맞아요: 카드 {check(sys.argv[1])}개")
    except Exception as error:  # noqa: BLE001
        print(f"형식이 맞지 않아요: {error}")
        sys.exit(1)
