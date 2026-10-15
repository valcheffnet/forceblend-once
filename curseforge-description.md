ForceBlend Once smooths the border between the terrain you already have and the terrain the game generates next to it. It is a fork of [ForceBlend](https://github.com/Mauro0805/ForceBlend) by mauro0805, updated for Minecraft 26.3.

When you change the world generator of an existing world, for example by removing a terrain mod like Terralith from a modpack, new chunks come out different from the old ones. Where they meet you get walls and cliffs. Vanilla Minecraft can blend that border, but only for chunks that are marked for it. ForceBlend marks them.

## What happens

The first time a world is opened with this mod, it:

1. zips the Overworld region files into `forceblendonce-backup-<date>.zip` in the world folder,
2. marks the chunks at the edge of the explored area for vanilla blending,
3. writes `forceblendonce.json` so it never runs on that world again.

After that you play as usual. When new chunks generate next to the marked ones, vanilla blends the two terrains together. On a world with about 13,000 chunks the first open took 13 seconds.

## Good to know

- Works in singleplayer and on dedicated servers. On a server it runs at startup, before the world is loaded, on the world set by `level-name` in `server.properties`. Players joining the server do not need the mod.
- It only blends old chunks with chunks that do not exist yet. If both sides of a border are already generated, delete the side you do not want first.
- If you removed a mod that added biomes or blocks, Fabric warns about missing content when you open the world. That warning is from Fabric, not from this mod.
- Keep your own backup of worlds you care about, even with the zip.

## Changes from ForceBlend

- Minecraft 26.3 support, tested in game.
- Runs once per world instead of on every open.
- Backup zip before anything is changed.
- Dedicated server support.
- Mod id `forceblendonce`.

Source and license (MIT): https://github.com/valcheffnet/forceblend-once
