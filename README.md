# ForceBlend Once

A fork of [ForceBlend](https://github.com/Mauro0805/ForceBlend) by mauro0805 for Minecraft 26.3, on Fabric and NeoForge.

**Download 1.1.0:** [Fabric jar](https://github.com/valcheffnet/forceblend-once/releases/download/v1.1.0/ForceBlendOnce-1.1.0.jar), [NeoForge jar](https://github.com/valcheffnet/forceblend-once/releases/download/v1.1.0-neoforge/ForceBlendOnce-neoforge-1.1.0.jar). Older versions are on the [releases page](https://github.com/valcheffnet/forceblend-once/releases), and the mod is also on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/forceblend-once).

ForceBlend marks the chunks at the edge of an exisiting Overworld so that vanilla terrain blending smooths the border when new chunks generate next to them. That helps after you change the world generator, for example when you remove a terrain mod like Terralith from a modpack. ForceBlend itself does not generate or move terrain, it only prepares the old chunks so vanilla can do the blending.

## What this fork changes

- Minecraft 26.3 is supported. It uses the same 26.x blending format as 26.1 and 26.2, and it was tested in game on 26.3.
- Each world is prepared once. ForceBlend Once writes `forceblendonce.json` into the world folder and does nothing on later opens.
- Before it changes anything, it zips the Overworld region files into `forceblendonce-backup-<date>.zip` in the world folder.
- The mod id is `forceblendonce`, so it can be told apart from the original.

- It also works on a dedicated server. There it runs at server start, before the world is opened, on the world named by `level-name` in `server.properties`.

Everything else is the original code. No Fabric API needed. On a server, players do not need the mod.

## Limits

Blending only works between old chunks and chunks that do not exist yet. If both sides of a border are already generated, delete the side you do not want first.

Keep your own backup of worlds you care about.

## NeoForge

The NeoForge version is in the `neoforge` folder and uses the same world code. It hooks `ServerAboutToStartEvent`, which NeoForge fires before the server loads its levels. That covers singleplayer and dedicated servers with one hook.

## Building

```
./gradlew build
```

Needs Java 25. The Fabric jar is in `build/libs`. For NeoForge run the same command inside `neoforge`; its jar ends up in `neoforge/build/libs`.

## License

MIT, like the original. See [LICENSE.txt](LICENSE.txt). Third party notices are in the jar under `third_party/`.
