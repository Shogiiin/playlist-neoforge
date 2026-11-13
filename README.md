# Playlist — NeoForge 21.1.256 (Minecraft 1.21.1) port

Unofficial port of [Playlist](https://github.com/fudgeu/playlist) 1.3.0 by Fudgeu (MIT, see `LICENSE`)
from Fabric 1.20.1 to NeoForge 21.1.256. The upstream source is not published, so this was
reconstructed by decompiling the 1.3.0 jar (Vineflower) and remapping intermediary → Mojang names.

## What changed vs. the Fabric version

| Fabric | NeoForge |
|---|---|
| `ClientModInitializer` (`PlaylistClient`) | `@Mod(value = "playlist", dist = Dist.CLIENT)` constructor |
| `ClientTickEvents.END_CLIENT_TICK` | `ClientTickEvent.Post` |
| `KeyBindingHelper.registerKeyBinding` | `RegisterKeyMappingsEvent` (`KeyBindHandler#register`) |
| `ResourceManagerHelper` reload listener | `RegisterClientReloadListenersEvent` |
| `InGameRenderMixin` (GameRenderer → InGameHud.render) | `RenderGuiEvent.Pre` (mixin removed) |
| `MusicTrackerMixin` | kept, cancels vanilla `MusicManager.tick()` |
| `FabricLoader` game dir / `isModLoaded` | `FMLPaths.GAMEDIR` / `ModList` |

1.20.1 → 1.21.1 API fixes: `ResourceLocation.fromNamespaceAndPath/parse`, new `Tesselator.begin` /
`BufferBuilder.addVertex` / `buildOrThrow` rendering, 4-arg `mouseScrolled`, 4-arg `renderBackground`.
The unused Kotlin `fluxui.newflux` package was dropped, so no Kotlin dependency is needed.

Port bug fixes: 1.21 renamed the vanilla music files (`calm1.ogg` → `minecraft.ogg`, …), so `sounds.json` was
remapped; icon-only buttons crashed 1.21 narration (null label); `c418.json` was Latin-1 (garbled "Oxygène").

## Additions over 1.3.0

- **1.21 music**: the 9 Tricky Trials background tracks, with vanilla 1.21.1 biome/menu conditions.
- **All 19 music discs**, filed under their official soundtrack albums (Samuel Åberg's "5" in `songinfo/samuel_aberg.json`).
- **Official album data**: album names, titles and track numbers follow the official soundtrack releases;
  Boss and 11 (never released on an album) are grouped as "Non-Album Tracks". Tricky Trials cover added.
- **Album browser**: "Albums" in the sidebar shows a cover grid in the main area (sidebar stays); open an album to
  see its track list, play it in order or shuffled, or start from any track. Album playback is gapless and then
  returns to the current playlist. Includes a "Music Discs" collection.
- **New songs reach existing playlists**: songs added in an update are appended to the default playlist once
  (tracked in `playlist/known_songs.json`, so songs you removed are not re-added).

## Build & test

Gradle runs on JDK 21+ (e.g. Java 25); the JDK 21 toolchain is downloaded automatically.

```sh
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk
./gradlew build       # jar -> build/libs/playlist-1.3.0-neoforge.jar
./gradlew runClient   # dev client with the mod loaded (run/ folder)
```

Manual test checklist (in a world):
- Default `G` key opens the Playlist screen; settings, create playlist, song info screens open/close.
- Music plays from the playlist (vanilla music is suppressed), skip / previous / play-stop keys work
  and show the now-playing widget on the HUD.
- Playlists persist in `run/playlist/` across restarts; settings in `run/config/playlist.json`.
- F3+T resource reload doesn't break anything.
