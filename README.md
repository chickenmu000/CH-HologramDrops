# CH-HologramDrops

Paper 1.21.x 용 **드롭 아이템 홀로그램 플러그인**입니다. 바닥에 떨어진 아이템 위에 `이름 x개수` 를 TextDisplay 홀로그램으로 표시합니다.
(igalexor 의 HologramDrops 1.0.0-BETA 동작을 참고해 새로 작성했고, 콘솔 경고가 나던 문제를 고쳤습니다.)

## 요구 사항
- Paper 1.21.x / Java 21

## 기능
- 떨어진 아이템 이름 + 개수 표시 (MiniMessage 로 색/그라데이션 자유롭게)
- 개수 1개일 때 `x1` 숨기기
- 월드별 켜기/끄기, 재질 블랙리스트, "직접 이름 지은 아이템만 표시"
- 재질별 표시 이름 바꾸기
- 아이템이 합쳐지거나 일부만 주워도 개수가 바로 갱신됨

## 고친 문제
원본은 아이템이 사라질 때(`EntityRemoveFromWorldEvent`) 홀로그램 엔티티를 **그 자리에서** 지웠습니다. 청크가 언로드되는 도중에 이 이벤트가 발생하면 서버가 엔티티 제거를 막고 아래 경고를 냅니다.

```
Entity TextDisplay[...] is currently prevented from being removed from the world since it is processing section status updates
```

이 플러그인은 홀로그램 제거를 항상 **다음 틱**에 처리해서 경고가 나지 않게 했습니다. 청크가 이미 언로드된 뒤라면 홀로그램도 같이 사라진 상태이므로 지울 것이 없습니다.

## 명령어
| 명령어 | 설명 | 권한 |
| --- | --- | --- |
| `/hdreload` (`/hdr`, `/holoreload`) | config.yml 다시 불러오기 | `chhologramdrops.reload` |

## config.yml
| 항목 | 기본값 | 설명 |
| --- | --- | --- |
| `hologram-format` | `<white><item> <gray>x<amount>` | 표시 형식 (`<item>` 이름, `<amount>` 개수) |
| `hide-single-count` | `true` | 1개면 개수 숨김 |
| `hologram-height-offset` | `0.75` | 아이템 위로 띄우는 높이 |
| `text-background-color` | `00000000` | 배경색 (AARRGGBB) |
| `text-scale` | `1.0` | 글자 크기 |
| `text-shadow` | `false` | 글자 그림자 |
| `enabled-worlds` | `[]` | 표시할 월드 (비우면 전체) |
| `blacklisted-materials` | `[]` | 홀로그램을 띄우지 않을 재질 |
| `custom-names-only` | `false` | 이름 지은 아이템만 표시 |
| `item-name-overrides` | `{}` | 재질별 표시 이름 |

## 참고
- 홀로그램은 저장되지 않는(`persistent=false`) 임시 엔티티입니다. 서버가 꺼지거나 청크가 언로드되면 사라지고, **이미 떨어져 있던 아이템에는 다시 생기지 않습니다.** 새로 떨어지는 아이템부터 표시됩니다.
- 원본에 있던 bStats(통계 전송)는 넣지 않았습니다.
- 원본 HologramDrops 와 같은 명령어(`/hdreload`)를 쓰므로 **원본 jar 는 지우고** 사용하세요.

## 빌드
```
mvn package
```
