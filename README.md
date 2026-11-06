# CH-HologramDrops

[한국어](#한국어) · [English](#english) · [中文](#中文) · [Español](#español)

---

## 한국어

Paper 1.21.x 용 **드롭 아이템 홀로그램 플러그인**입니다. 바닥에 떨어진 아이템 위에 `이름 x개수` 를 TextDisplay 홀로그램으로 표시합니다.
(igalexor 의 HologramDrops 1.0.0-BETA 동작을 참고해 새로 작성했고, 콘솔 경고가 나던 문제를 고쳤습니다.)

### 요구 사항
- Paper 1.21.x / Java 21

### 기능
- 떨어진 아이템 이름 + 개수 표시 (MiniMessage 로 색/그라데이션 자유롭게)
- 개수 1개일 때 `x1` 숨기기
- 월드별 켜기/끄기, 재질 블랙리스트, "직접 이름 지은 아이템만 표시"
- 재질별 표시 이름 바꾸기
- 아이템이 합쳐지거나 일부만 주워도 개수가 바로 갱신됨

### 고친 문제
원본은 아이템이 사라질 때(`EntityRemoveFromWorldEvent`) 홀로그램 엔티티를 **그 자리에서** 지웠습니다. 청크가 언로드되는 도중에 이 이벤트가 발생하면 서버가 엔티티 제거를 막고 아래 경고를 냅니다.

```
Entity TextDisplay[...] is currently prevented from being removed from the world since it is processing section status updates
```

이 플러그인은 홀로그램 제거를 항상 **다음 틱**에 처리해서 경고가 나지 않게 했습니다. 청크가 이미 언로드된 뒤라면 홀로그램도 같이 사라진 상태이므로 지울 것이 없습니다.

### 명령어
| 명령어 | 설명 | 권한 |
| --- | --- | --- |
| `/hdreload` (`/hdr`, `/holoreload`) | config.yml 다시 불러오기 | `chhologramdrops.reload` |

### config.yml
| 항목 | 기본값 | 설명 |
| --- | --- | --- |
| `language` | `ko` | 콘솔/리로드 메시지 언어 (`ko` `en` `zh` `es`) |
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

### 참고
- 홀로그램은 저장되지 않는(`persistent=false`) 임시 엔티티입니다. 서버가 꺼지거나 청크가 언로드되면 사라지고, **이미 떨어져 있던 아이템에는 다시 생기지 않습니다.** 새로 떨어지는 아이템부터 표시됩니다.
- 원본에 있던 bStats(통계 전송)는 넣지 않았습니다.
- 원본 HologramDrops 와 같은 명령어(`/hdreload`)를 쓰므로 **원본 jar 는 지우고** 사용하세요.

### 빌드
```
mvn package
```

---

## English

A **dropped-item hologram plugin** for Paper 1.21.x. It shows `name xAmount` as a TextDisplay hologram above items lying on the ground.
(Written from scratch based on the behavior of igalexor's HologramDrops 1.0.0-BETA, with the console warning fixed.)

### Requirements
- Paper 1.21.x / Java 21

### Features
- Item name + amount (MiniMessage: colors, gradients, ...)
- Hide `x1` for single items
- Per-world enable list, material blacklist, "custom-named items only"
- Per-material display name overrides
- Amount updates instantly when items merge or are partially picked up
- Console / reload message language: `language: ko | en | zh | es`

### The fixed problem
The original removed the hologram entity **immediately** inside `EntityRemoveFromWorldEvent`. When that event fires while a chunk is unloading, the server refuses the removal and logs:
`Entity TextDisplay[...] is currently prevented from being removed from the world since it is processing section status updates`.
This plugin always removes holograms on the **next tick**, so the warning no longer appears.

### Command
| Command | Description | Permission |
| --- | --- | --- |
| `/hdreload` (`/hdr`, `/holoreload`) | Reload config.yml | `chhologramdrops.reload` |

### Notes
- Holograms are non-persistent entities: they disappear on restart / chunk unload and are **not recreated for items already on the ground**; new drops get them.
- `custom-names-only: true` shows holograms only for custom-named items (vanilla items get none).
- bStats (telemetry) from the original is not included.
- It uses the same command as the original, so **remove the original HologramDrops jar**.

---

## 中文

适用于 Paper 1.21.x 的**掉落物全息文字插件**。在地面掉落物上方以 TextDisplay 全息文字显示 `名称 x数量`。
（参考 igalexor 的 HologramDrops 1.0.0-BETA 的行为重新编写，并修复了控制台警告问题。）

### 运行要求
- Paper 1.21.x / Java 21

### 功能
- 显示物品名称和数量（支持 MiniMessage：颜色、渐变等）
- 数量为 1 时隐藏 `x1`
- 按世界启用、材质黑名单、"仅显示自定义名称的物品"
- 按材质覆盖显示名称
- 物品合并或被部分拾取时数量即时更新
- 控制台 / 重载提示语言：`language: ko | en | zh | es`

### 已修复的问题
原版在 `EntityRemoveFromWorldEvent` 中**立即**移除全息实体。当该事件在区块卸载过程中触发时，服务器会拒绝移除并输出警告：
`Entity TextDisplay[...] is currently prevented from being removed from the world since it is processing section status updates`。
本插件始终在**下一个 tick** 移除全息文字，因此不再出现该警告。

### 命令
| 命令 | 说明 | 权限 |
| --- | --- | --- |
| `/hdreload`（`/hdr`、`/holoreload`） | 重新加载 config.yml | `chhologramdrops.reload` |

### 注意
- 全息文字是不保存的临时实体：重启或区块卸载后会消失，**已在地面上的物品不会重新生成**，新掉落的物品才会显示。
- `custom-names-only: true` 时只有自定义名称的物品显示全息文字（原版物品不显示）。
- 未包含原版的 bStats（统计上报）。
- 命令与原版相同，请**删除原版 HologramDrops jar**。

---

## Español

**Plugin de hologramas para objetos soltados** en Paper 1.21.x. Muestra `nombre xcantidad` como holograma TextDisplay sobre los objetos que están en el suelo.
(Escrito desde cero basándose en el comportamiento de HologramDrops 1.0.0-BETA de igalexor, con el aviso de consola corregido.)

### Requisitos
- Paper 1.21.x / Java 21

### Funciones
- Nombre y cantidad del objeto (MiniMessage: colores, degradados, ...)
- Oculta `x1` cuando hay un solo objeto
- Mundos habilitados, lista negra de materiales, "solo objetos con nombre personalizado"
- Nombres personalizados por material
- La cantidad se actualiza al instante cuando los objetos se combinan o se recogen parcialmente
- Idioma de la consola y del mensaje de recarga: `language: ko | en | zh | es`

### Problema corregido
El original eliminaba el holograma **inmediatamente** dentro de `EntityRemoveFromWorldEvent`. Si ese evento ocurre mientras se descarga un chunk, el servidor rechaza la eliminación y muestra:
`Entity TextDisplay[...] is currently prevented from being removed from the world since it is processing section status updates`.
Este plugin elimina los hologramas siempre en el **siguiente tick**, por lo que el aviso ya no aparece.

### Comando
| Comando | Descripción | Permiso |
| --- | --- | --- |
| `/hdreload` (`/hdr`, `/holoreload`) | Recarga config.yml | `chhologramdrops.reload` |

### Notas
- Los hologramas son entidades no persistentes: desaparecen al reiniciar o descargar el chunk y **no se recrean para los objetos que ya estaban en el suelo**; los nuevos objetos sí los reciben.
- Con `custom-names-only: true` solo los objetos con nombre personalizado tienen holograma (los objetos vanilla no).
- No incluye bStats (telemetría) del original.
- Usa el mismo comando que el original, así que **elimina el jar original de HologramDrops**.
