# LoopHarbor AI 팁

[LoopHarbor](https://github.com/L-archive) 앱의 "AI 팁" 메뉴에 나오는 작업 지침 묶음이에요.
LoopHarbor의 AI가 특정 일(버그 요청 정리, 커밋, 코드 리뷰, PR, 핸드오프 등)을 더 잘하도록 돕는 지침을 공식 문서, 인기 저장소, 커뮤니티에서 조사해 정리해요.

## 어떻게 만들어지나

1. 조사하는 AI가 공식 문서, 평이 좋은 저장소, 커뮤니티를 읽고 카드 초안을 만들어요. 카드는 원문을 옮기지 않고 우리 말로 다시 쓰고, 출처 링크와 확인한 날짜를 달아요.
2. 검토하는 AI가 따로 출처를 다시 열어, 카드 내용이 출처와 맞는지, 인기 표시(별 수 등)가 맞는지, 권한이나 승인을 풀거나 비밀정보를 다루는 위험한 규칙이 없는지 확인해요.
3. 바뀐 내용은 Draft PR로 올라오고, 관리자가 확인한 뒤에만 main에 들어가요.
4. main에 들어가면 GitHub Actions가 형식을 확인하고 서명해요(`pack.json.sig`).

## 앱이 받는 방식

- 앱은 하루 한 번 `pack.json`과 `pack.json.sig`를 받아요. 주소 요청만 하고 사용자 정보는 보내지 않아요.
- 앱에 들어 있는 공개키로 서명을 확인해서, 서명이 맞는 묶음만 써요.
- 사용자가 적용한 지침은 id만 사용자 PC에 남아요. 지침 내용은 늘 서명된 묶음에서 읽어서, 검토를 거쳐 고친 지침은 앱 업데이트 없이 반영돼요.
- 적용한 지침은 AI가 일할 때 함께 받고, "when"이 맞는 일에만 따라요. 프로젝트의 규칙 파일(AGENTS.md, CONTRIBUTING.md)과 다르면 프로젝트 규칙이 먼저예요.
- 지침은 사용자의 "내 방식"(말투, 이름, 나만의 규칙)과 따로 관리해요.

## 파일

| 파일 | 설명 |
|---|---|
| `pack.json` | 지침 묶음 (format 2) |
| `pack.json.sig` | Ed25519 서명 (Actions가 만들어요) |
| `tools/check_pack.py` | 형식 확인 (앱과 같은 규칙) |
| `tools/SignPack.java` | 서명 도구 |

## 지침 형식 (format 2)

| 칸 | 설명 |
|---|---|
| `id` | 영어 소문자, 숫자, 하이픈 |
| `moment` | 쓰이는 순간: ask(요청을 받을 때), fix(고칠 때), check(확인할 때), hand(넘길 때), reply(답장할 때) |
| `roles` | developer, designer, planner, all |
| `title` | 지침 이름 (80자) |
| `when` | 이 지침을 따르는 상황 (120자) |
| `why` | 좋은 점 한 줄 (200자) |
| `steps` | AI가 받는 지침 1~8줄 (한 줄 200자) |
| `source` | `title`, `url`(https) |
| `checkedAt`, `addedAt`, `updatedAt` | 날짜. `updatedAt`과 `change`(고친 점, 120자)는 고친 지침에만 |
| `popularity` | `basis`(official, github, community), `note` |

형식이 하나라도 어긋나면 앱은 묶음 전체를 받지 않아요. 지침은 권한, 승인, 비밀정보 처리를 바꾸는 내용을 담지 않아요.
