<div align="center">
<img src="assets/purpur_bars_fit.svg" height="256">
</div>

# Purpur Bars
Just a convenient plugin implementation of Purpur's 
[`/tpsbar`](https://github.com/PurpurMC/Purpur/commit/42bf7db01862c6bc9ed3441cd252655243b3a505) and 
[`/rambar`](https://github.com/PurpurMC/Purpur/commit/e13b74d31e76acb8fdd3023a176382f846e0b248) for non Purpur-based servers

> [!NOTE]
> Includes `/ram` command!

## Screenshot
<div align="center">
<img src="assets/purpurbars_example.png">
</div>

## Commands
* `/purpurbars reload`: Reload plugin configuration
* `/tpsbar`: Shows Purpur's TPS bar
* `/rambar`: Shows Purpur's RAM bar
* `/compass`: Shows Purpur's Compass bar
* `/ram`: Display RAM usage in chat

## Permissions
* `purpurbars.admin`: Reload the plugin
* `purpurbars.monitor.tps`: Use `/tpsbar` command
* `purpurbars.monitor.ram`: Use `/rambar` command
* `purpurbars.monitor.compass`: Use `/compass` command
* `purpurbars.check.ram`: Use `/ram` command

## How to compile
Just compile using gradle in the root project directory
```shell
./gradlew build
```

## Credits
All credits to [PurpurMC Team](https://github.com/PurpurMC/Purpur/)
