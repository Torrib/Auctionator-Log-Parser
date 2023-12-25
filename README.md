# Auctionator Log Parser
Simple project that parses Auctionator logs and produces a map of items and prices.

## Instructions

### Library
1. Import auctionator-parser

```
<dependency>
    <groupId>com.tgoblin</groupId>
    <artifactId>auctionator-parser</artifactId>
    <version>1.0.0</version>
</dependency>
```

2. Call `AuctionatorParser.parse([path_to_Auctionator.lua])`

### Running Manually
This simple example script will parse the log file and produce a json with the information.
1. Copy `template.env` to `.env` and add update the properties
2. Run **Main.java** from the example module.

### Auctionator.lua
One example Auctionator log file location is:
`C:/Program Files (x86)/World of Warcraft/_classic_era_/WTF/Account/[ACCOUNT_NAME]/SavedVariables/Auctionator.lua`
Where [ACCOUNT_NAME] can be replaced with your account name.
_classic_era_ will need to be replaced depending on the version of wow you want to parse.
