<div align="center">
<img src="assets/purpur_bars_fit.svg" height="256">
</div>

# Purpur Bars
Just a convenient plugin implementation of Purpur's 
[`/tpsbar`](https://github.com/PurpurMC/Purpur/commit/42bf7db01862c6bc9ed3441cd252655243b3a505) and 
[`/rambar`](https://github.com/PurpurMC/Purpur/commit/e13b74d31e76acb8fdd3023a176382f846e0b248) for non Purpur-based servers

## Commands
* `/purpurbars reload`: Reload plugin configuration
* `/rambar`: Shows Purpur's RAM bar
* `/tpsbar`: Shows Purpur's TPS bar

## Permissions
* `purpurbars.admin`: Reload the plugin
* `purpurbars.monitor.ram`: Use `/rambar` command
* `purpurbars.monitor.tps`: Use `/tpsbar` command

## How to compile
Just compile using gradle in the root project directory
```shell
./gradlew build
```

## Credits
All credits to [PurpurMC Team](https://github.com/PurpurMC/Purpur/)
