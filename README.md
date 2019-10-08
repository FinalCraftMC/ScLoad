# ScLoad for WorldEdit 7

**ScLoad** is a Spigot plugin enabling schematics to be placed programmatically
with the Minecraft command line. It relies on WorldEdit for schematic reading and
placement.

Original versions of ScLoad were created to work with WorldEdit versions 6 and 
earlier. However, several breaking changes were made in the API to WorldEdit 7,
without even deprecating the older functionality first. This causes previous
versions of ScLoad or any other WorldEdit-reliant plugins to immediately break when
run with WorldEdit 7.

**ScLoad for WorldEdit 7** is a new version of ScLoad which utilizes the new
WorldEdit 7 API, enabling it to work on modern versions of WorldEdit. It is also 
compatible with Fast Async WorldEdit 1.13 (breaking), which is itself based on
WorldEdit 7.

## License

This project is licensed under the GNU General Public License v3, as the original
ScLoad was also licensed under it. Please see the LICENSE file for more information.

**NOTE:** Do not expect this project to be actively maintained. You are free
to fork this project and create further derivatives provided that you follow
the GNU GPLv3 License. See the LICENSE file for details.
