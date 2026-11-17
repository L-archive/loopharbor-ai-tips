# LoopHarbor AI 팁

[LoopHarbor](https://github.com/L-archive) 앱의 "AI 팁" 메뉴와 인터뷰에 나오는 추천 카드 묶음이에요.
일하는 규칙, 답장 말투, AI 활용 팁을 공식 문서, 인기 저장소, 커뮤니티에서 조사해 카드로 정리해요.

## 어떻게 만들어지나

1. 조사하는 AI가 공식 문서, 평이 좋은 저장소, 커뮤니티를 읽고 카드 초안을 만들어요. 카드는 원문을 옮기지 않고 우리 말로 다시 쓰고, 출처 링크와 확인한 날짜를 달아요.
2. 검토하는 AI가 따로 출처를 다시 열어, 카드 내용이 출처와 맞는지, 인기 표시(별 수 등)가 맞는지, 권한이나 승인을 풀거나 비밀정보를 다루는 위험한 규칙이 없는지 확인해요.
3. 바뀐 내용은 Draft PR로 올라오고, 관리자가 확인한 뒤에만 main에 들어가요.
4. main에 들어가면 GitHub Actions가 형식을 확인하고 서명해요(`pack.json.sig`).

## 앱이 받는 방식

- 앱은 하루 한 번 `pack.json`과 `pack.json.sig`를 받아요. 주소 요청만 하고 사용자 정보는 보내지 않아요.
- 앱에 들어 있는 공개키로 서명을 확인해서, 서명이 맞는 묶음만 써요.
- 사용자가 고른 카드는 사용자 PC의 "내 방식"에 복사돼요. 새 묶음이 와도 이미 고른 것은 바뀌지 않아요.

## 파일

| 파일 | 설명 |
|---|---|
| `pack.json` | 카드 묶음 (format 1) |
| `pack.json.sig` | Ed25519 서명 (Actions가 만들어요) |
| `tools/check_pack.py` | 형식 확인 (앱과 같은 규칙) |
| `tools/SignPack.java` | 서명 도구 |

## 카드 형식

`id`, `kind`(rule, tone, example, tip), `roles`(developer, designer, planner, all), `topic`, `title`, `text`, `why`,
`source`(`title`, `url`), `checkedAt`, `addedAt`, `popularity`(`basis`: official, github, community, `note`).
형식이 하나라도 어긋나면 앱은 묶음 전체를 받지 않아요.
