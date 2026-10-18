# Docs | FACEIT for Developers

[Skip to main content](https://docs.faceit.com/docs/data-api/data/#__docusaurus_skipToContent_fallback)

- This API provide access to FACEIT's data
- Championships
- Games
- Hubs
- Leaderboards
- Leagues
- Matches
- Matchmakings
- Organizers
- Players
  - getRetrieve player details
  - getRetrieve player details
  - getRetrieve all bans of a player
  - getRetrieve statistics of a player for a given amount of matches
  - getRetrieve all matches of a player
  - getRetrieve all hubs of a player
  - getRetrieve statistics of a player
  - getRetrieve all teams of a player
  - getRetrieve all tournaments of a player
- Rankings
- Search
- Teams
- Tournaments

[API docs by Redocly](https://redocly.com/redoc/)

# Data API (4)

Download OpenAPI specification:[Download](https://open.faceit.com/data/v4/docs/swagger.json)

License: Property of FACEIT LTD

# [section/This-API-provide-access-to-FACEIT's-data](https://docs.faceit.com/docs/data-api/data/#section/This-API-provide-access-to-FACEIT's-data)This API provide access to FACEIT's data

# [tag/Championships](https://docs.faceit.com/docs/data-api/data/#tag/Championships)Championships

## [tag/Championships/operation/getChampionships](https://docs.faceit.com/docs/data-api/data/#tag/Championships/operation/getChampionships)Retrieve all championships of a game

Retrieve all championships of a game

##### Authorizations:

*key*

##### query Parameters

| gamerequired | string The id of the game |
| --- | --- |
| type | string Enum: "all" "upcoming" "ongoing" "past" Kind of matches to return. Can be all(default), upcoming, ongoing or past |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 10 ] Default: 10 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Championships/operation/getChampionship](https://docs.faceit.com/docs/data-api/data/#tag/Championships/operation/getChampionship)Retrieve championship details

Retrieve championship details

##### Authorizations:

*key*

##### path Parameters

| championship_idrequired | string The id of the championship |
| --- | --- |

##### query Parameters

| expanded | Array ofstringsunique Items Enum: "organizer" "game" List of entity names to expand in request |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"anticheat_required": true,"avatar": "string","background_image": "string","championship_id": "string","championship_start": 0,"checkin_clear": 0,"checkin_enabled": true,"checkin_start": 0,"cover_image": "string","current_subscriptions": 0,"description": "string","faceit_url": "string","featured": true,"full": true,"game_data": {"assets": {},"game_id": "string","long_label": "string","order": 0,"parent_game_id": "string","platforms": [],"regions": [],"short_label": "string"},"game_id": "string","id": "string","join_checks": {"allowed_team_types": [],"blacklist_geo_countries": [],"join_policy": "string","max_skill_level": 0,"membership_type": "string","min_skill_level": 0,"whitelist_geo_countries": [],"whitelist_geo_countries_min_players": 0},"name": "string","organizer_data": {"avatar": "string","cover": "string","description": "string","facebook": "string","faceit_url": "string","followers_count": 0,"name": "string","organizer_id": "string","twitch": "string","twitter": "string","type": "string","vk": "string","website": "string","youtube": "string"},"organizer_id": "string","prizes": [{}],"region": "string","rules_id": "string","schedule": {"property1": {},"property2": {}},"screening": {"enabled": true,"id": "string"},"seeding_strategy": "string","slots": 0,"status": "string","stream": {"active": true,"platform": "string","source": "string","title": "string"},"subscription_end": 0,"subscription_start": 0,"subscriptions_locked": true,"substitution_configuration": {"max_substitutes": 0,"max_substitutions": 0},"total_groups": 0,"total_prizes": 0,"total_rounds": 0,"type": "string"}`

## [tag/Championships/operation/getChampionshipMatches](https://docs.faceit.com/docs/data-api/data/#tag/Championships/operation/getChampionshipMatches)Retrieve all matches of a championship

Retrieve all matches of a championship

##### Authorizations:

*key*

##### path Parameters

| championship_idrequired | string The id of the championship |
| --- | --- |

##### query Parameters

| type | string Enum: "all" "upcoming" "ongoing" "past" Kind of matches to return. Can be all(default), upcoming, ongoing or past |
| --- | --- |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Championships/operation/getChampionshipResults](https://docs.faceit.com/docs/data-api/data/#tag/Championships/operation/getChampionshipResults)Retrieve all results of a championship

Retrieve all results of a championship

##### Authorizations:

*key*

##### path Parameters

| championship_idrequired | string The id of the championship |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Championships/operation/getChampionshipSubscriptions](https://docs.faceit.com/docs/data-api/data/#tag/Championships/operation/getChampionshipSubscriptions)Retrieve all subscriptions of a championship

Retrieve all subscriptions of a championship

##### Authorizations:

*key*

##### path Parameters

| championship_idrequired | string The id of the championship |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 10 ] Default: 10 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

# [tag/Games](https://docs.faceit.com/docs/data-api/data/#tag/Games)Games

## [tag/Games/operation/getAllGames](https://docs.faceit.com/docs/data-api/data/#tag/Games/operation/getAllGames)Retrieve details of all games on FACEIT

Retrieve details of all games on FACEIT

##### Authorizations:

*key*

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Games/operation/getGameMatchmakings](https://docs.faceit.com/docs/data-api/data/#tag/Games/operation/getGameMatchmakings)Retrieve details of all matchmakings of a game on FACEIT

Retrieve details of all matchmakings of a game on FACEIT

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string Example: cs2 The id of the game |
| --- | --- |

##### query Parameters

| region | string Example: region=EU The region of the matchmakings |
| --- | --- |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Games/operation/getGame](https://docs.faceit.com/docs/data-api/data/#tag/Games/operation/getGame)Retrieve game details

Retrieve game details

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string The id of the game |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"assets": {"cover": "string","featured_img_l": "string","featured_img_m": "string","featured_img_s": "string","flag_img_icon": "string","flag_img_l": "string","flag_img_m": "string","flag_img_s": "string","landing_page": "string"},"game_id": "string","long_label": "string","order": 0,"parent_game_id": "string","platforms": ["string"],"regions": ["string"],"short_label": "string"}`

## [tag/Games/operation/getParentGame](https://docs.faceit.com/docs/data-api/data/#tag/Games/operation/getParentGame)Retrieve the details of the parent game, if the game is region-specific

Retrieve the details of the parent game, if the game is region-specific

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string The id of the game |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"assets": {"cover": "string","featured_img_l": "string","featured_img_m": "string","featured_img_s": "string","flag_img_icon": "string","flag_img_l": "string","flag_img_m": "string","flag_img_s": "string","landing_page": "string"},"game_id": "string","long_label": "string","order": 0,"parent_game_id": "string","platforms": ["string"],"regions": ["string"],"short_label": "string"}`

## [tag/Games/operation/getQueuesByEntityFilters](https://docs.faceit.com/docs/data-api/data/#tag/Games/operation/getQueuesByEntityFilters)Retrieve queues by filters on FACEIT

Retrieve queues by filters on FACEIT. Available only for game developers and queue owners(organizers)

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string Example: csgo The id of the game |
| --- | --- |

##### query Parameters

| entity_typerequired | string Example: entity_type=hub The type of the entity |
| --- | --- |
| entity_idrequired | string The id of the entity |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 20 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Games/operation/getQueueById](https://docs.faceit.com/docs/data-api/data/#tag/Games/operation/getQueueById)Retrieve details of a queue on FACEIT

Retrieve details of a queue on FACEIT. Available only for game or queue owners(organizers)

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string Example: cs2 The id of the game |
| --- | --- |
| queue_idrequired | string The id of the queue |

### Responses

### Response samples

- 200

Content type

application/json

`{"adminTool": true,"anticheatRequired": true,"calculateElo": true,"captainSelection": "string","checkIn": {"time": 0},"entityId": "string","entityType": "string","fbiManagement": true,"game": "string","groupSimilar": true,"id": "string","joinType": {"maxParty": 0,"party": true,"premade": true,"solo": true},"lastModified": "2019-08-24T14:15:22Z","maxSkill": 0,"minSkill": 0,"noOfPlayers": 0,"open": true,"organizerId": "string","paused": true,"queueAlgorithm": {"algorithmId": "string","algorithmInput": [],"algorithmParameters": {},"geoDescription": {},"geoLabel": {},"id": "string","roleBasedCaptainPick": true},"queueName": "string","region": "string","state": "string","verifiedMatching": true}`

## [tag/Games/operation/getQueueBans](https://docs.faceit.com/docs/data-api/data/#tag/Games/operation/getQueueBans)Retrieve queue bans on FACEIT

Retrieve queue bans on FACEIT. Available only for game or queue owners(organizers)

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string Example: eu The id of the game |
| --- | --- |
| queue_idrequired | string The id of the queue |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Games/operation/getQueuesByRegion](https://docs.faceit.com/docs/data-api/data/#tag/Games/operation/getQueuesByRegion)Retrieve queues by region on FACEIT

Retrieve queues by region on FACEIT. Available only for game developers

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string Example: csgo The id of the game |
| --- | --- |
| region_idrequired | string Example: eu The id of the region |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 20 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

# [tag/Hubs](https://docs.faceit.com/docs/data-api/data/#tag/Hubs)Hubs

## [tag/Hubs/operation/getHub](https://docs.faceit.com/docs/data-api/data/#tag/Hubs/operation/getHub)Retrieve hub details

Retrieve hub details

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |

##### query Parameters

| expanded | Array ofstringsunique Items Enum: "organizer" "game" List of entity names to expand in request |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"avatar": "string","background_image": "string","chat_room_id": "string","cover_image": "string","description": "string","faceit_url": "string","game_data": {"assets": {},"game_id": "string","long_label": "string","order": 0,"parent_game_id": "string","platforms": [],"regions": [],"short_label": "string"},"game_id": "string","hub_id": "string","join_permission": "string","max_skill_level": 0,"min_skill_level": 0,"name": "string","organizer_data": {"avatar": "string","cover": "string","description": "string","facebook": "string","faceit_url": "string","followers_count": 0,"name": "string","organizer_id": "string","twitch": "string","twitter": "string","type": "string","vk": "string","website": "string","youtube": "string"},"organizer_id": "string","players_joined": 0,"region": "string","rule_id": "string"}`

## [tag/Hubs/operation/getHubMatches](https://docs.faceit.com/docs/data-api/data/#tag/Hubs/operation/getHubMatches)Retrieve all matches of a hub

Retrieve all matches of a hub

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |

##### query Parameters

| type | string Enum: "all" "upcoming" "ongoing" "past" Kind of matches to return. Can be all(default), upcoming, ongoing or past |
| --- | --- |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Hubs/operation/getHubMembers](https://docs.faceit.com/docs/data-api/data/#tag/Hubs/operation/getHubMembers)Retrieve all members of a hub

Retrieve all members of a hub

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |

##### query Parameters

| offset | integer[ 0 .. 1000 ] Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 50 ] Default: 50 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Hubs/operation/getHubRoles](https://docs.faceit.com/docs/data-api/data/#tag/Hubs/operation/getHubRoles)Retrieve all roles members can have in a hub

Retrieve all roles members can have in a hub

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |

##### query Parameters

| offset | integer[ 0 .. 1000 ] Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 50 ] Default: 50 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Hubs/operation/getHubRules](https://docs.faceit.com/docs/data-api/data/#tag/Hubs/operation/getHubRules)Retrieve rules of a hub

Retrieve rules of a hub

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"body": "string","game": "string","name": "string","organizer": "string","rule_id": "string"}`

## [tag/Hubs/operation/getHubStats](https://docs.faceit.com/docs/data-api/data/#tag/Hubs/operation/getHubStats)Retrieve statistics of a hub

Retrieve statistics of a hub

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"game_id": "string","players": [{}]}`

# [tag/Leaderboards](https://docs.faceit.com/docs/data-api/data/#tag/Leaderboards)Leaderboards

## [tag/Leaderboards/operation/getChampionshipLeaderboards](https://docs.faceit.com/docs/data-api/data/#tag/Leaderboards/operation/getChampionshipLeaderboards)Retrieve all leaderboards of a championship

Retrieve all leaderboards of a championship

##### Authorizations:

*key*

##### path Parameters

| championship_idrequired | string The id of the championship |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Leaderboards/operation/getChampionshipGroupRanking](https://docs.faceit.com/docs/data-api/data/#tag/Leaderboards/operation/getChampionshipGroupRanking)Retrieve group ranking of a championship

Retrieve group ranking of a championship

##### Authorizations:

*key*

##### path Parameters

| championship_idrequired | string The id of the championship |
| --- | --- |
| grouprequired | integer A group of the championship |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"leaderboard": {"competition_id": "string","competition_type": "string","end_date": 0,"game_id": "string","group": 0,"leaderboard_id": "string","leaderboard_mode": "string","leaderboard_name": "string","leaderboard_type": "string","min_matches": 0,"points_per_draw": 0,"points_per_loss": 0,"points_per_win": 0,"points_type": "string","ranking_boost": 0,"ranking_type": "string","region": "string","round": 0,"season": 0,"start_date": 0,"starting_points": 0,"status": "string"},"start": 0}`

## [tag/Leaderboards/operation/getHubLeaderboards](https://docs.faceit.com/docs/data-api/data/#tag/Leaderboards/operation/getHubLeaderboards)Retrieve all leaderboards of a hub

Retrieve all leaderboards of a hub

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Leaderboards/operation/getHubRanking](https://docs.faceit.com/docs/data-api/data/#tag/Leaderboards/operation/getHubRanking)Retrieve all time ranking of a hub

Retrieve all time ranking of a hub

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"leaderboard": {"competition_id": "string","competition_type": "string","end_date": 0,"game_id": "string","group": 0,"leaderboard_id": "string","leaderboard_mode": "string","leaderboard_name": "string","leaderboard_type": "string","min_matches": 0,"points_per_draw": 0,"points_per_loss": 0,"points_per_win": 0,"points_type": "string","ranking_boost": 0,"ranking_type": "string","region": "string","round": 0,"season": 0,"start_date": 0,"starting_points": 0,"status": "string"},"start": 0}`

## [tag/Leaderboards/operation/getHubSeasonRanking](https://docs.faceit.com/docs/data-api/data/#tag/Leaderboards/operation/getHubSeasonRanking)Retrieve seasonal ranking of a hub

Retrieve seasonal ranking of a hub

##### Authorizations:

*key*

##### path Parameters

| hub_idrequired | string The id of the hub |
| --- | --- |
| seasonrequired | integer A season of the hub |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"leaderboard": {"competition_id": "string","competition_type": "string","end_date": 0,"game_id": "string","group": 0,"leaderboard_id": "string","leaderboard_mode": "string","leaderboard_name": "string","leaderboard_type": "string","min_matches": 0,"points_per_draw": 0,"points_per_loss": 0,"points_per_win": 0,"points_type": "string","ranking_boost": 0,"ranking_type": "string","region": "string","round": 0,"season": 0,"start_date": 0,"starting_points": 0,"status": "string"},"start": 0}`

## [tag/Leaderboards/operation/getLeaderboard](https://docs.faceit.com/docs/data-api/data/#tag/Leaderboards/operation/getLeaderboard)Retrieve ranking from a leaderboard id

Retrieve ranking from a leaderboard id

##### Authorizations:

*key*

##### path Parameters

| leaderboard_idrequired | string The id of the leaderboard |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"leaderboard": {"competition_id": "string","competition_type": "string","end_date": 0,"game_id": "string","group": 0,"leaderboard_id": "string","leaderboard_mode": "string","leaderboard_name": "string","leaderboard_type": "string","min_matches": 0,"points_per_draw": 0,"points_per_loss": 0,"points_per_win": 0,"points_type": "string","ranking_boost": 0,"ranking_type": "string","region": "string","round": 0,"season": 0,"start_date": 0,"starting_points": 0,"status": "string"},"start": 0}`

## [tag/Leaderboards/operation/getPlayerRankingInLeaderboard](https://docs.faceit.com/docs/data-api/data/#tag/Leaderboards/operation/getPlayerRankingInLeaderboard)Retrieve a players ranking in a leaderboard

Retrieve a players ranking in a leaderboard

##### Authorizations:

*key*

##### path Parameters

| leaderboard_idrequired | string The id of the leaderboard |
| --- | --- |
| player_idrequired | string The id of the player |

### Responses

### Response samples

- 200

Content type

application/json

`{"current_streak": 0,"draw": 0,"lost": 0,"played": 0,"player": {"avatar": "https://avatar.jpg","country": "GB","faceit_url": "string","membership_type": "string","memberships": [],"nickname": "nickname","skill_level": 1,"user_id": "6204037c-30e6-408b-8aaa-dd8219860b4b"},"points": 0,"position": 0,"win_rate": 0,"won": 0}`

# [tag/Leagues](https://docs.faceit.com/docs/data-api/data/#tag/Leagues)Leagues

## [tag/Leagues/operation/getLeagueById](https://docs.faceit.com/docs/data-api/data/#tag/Leagues/operation/getLeagueById)Retrieve details of a league of a matchmaking on FACEIT

Retrieve details of a league of a matchmaking on FACEIT

##### Authorizations:

*key*

##### path Parameters

| league_idrequired | string The id of the league |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"divisions": [{}],"game": "string","id": "string","min_matches": 0,"region": "string","season": {"end_date": "2019-08-24T14:15:22Z","number": 0,"placement_match_count": 0,"start_date": "2019-08-24T14:15:22Z"}}`

## [tag/Leagues/operation/getLeagueSeason](https://docs.faceit.com/docs/data-api/data/#tag/Leagues/operation/getLeagueSeason)Retrieve details of a season of a league on FACEIT

Retrieve details of a season of a league on FACEIT

##### Authorizations:

*key*

##### path Parameters

| league_idrequired | string Example: 6e7daaee-8b9d-4fbf-86c4-361fa7e579fc The id of the league |
| --- | --- |
| season_idrequired | integer Example: 1 The id of the season |

### Responses

### Response samples

- 200

Content type

application/json

`{"divisions": [{}],"season": {"end_date": "2019-08-24T14:15:22Z","number": 0,"placement_match_count": 0,"start_date": "2019-08-24T14:15:22Z"}}`

## [tag/Leagues/operation/getPlayerForLeagueSeason](https://docs.faceit.com/docs/data-api/data/#tag/Leagues/operation/getPlayerForLeagueSeason)Retrieve details of a player for a given league and season on FACEIT

Retrieve details of a player for a given league and season on FACEIT

##### Authorizations:

*key*

##### path Parameters

| league_idrequired | string Example: 6e7daaee-8b9d-4fbf-86c4-361fa7e579fc The id of the league |
| --- | --- |
| season_idrequired | integer Example: 1 The id of the season |
| player_idrequired | string Example: 6e7daaee-8b9d-4fbf-86c4-361fa7e579fc The id of the player |

### Responses

### Response samples

- 200

Content type

application/json

`{"division_name": "string","division_tier": "string","division_type": "string","leaderboard_id": "string","points": 0,"position": 0}`

# [tag/Matches](https://docs.faceit.com/docs/data-api/data/#tag/Matches)Matches

## [tag/Matches/operation/getMatch](https://docs.faceit.com/docs/data-api/data/#tag/Matches/operation/getMatch)Retrieve match details

Retrieve match details

##### Authorizations:

*key*

##### path Parameters

| match_idrequired | string The id of the match |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"best_of": 0,"broadcast_start_time": 0,"broadcast_start_time_label": "string","calculate_elo": true,"chat_room_id": "string","competition_id": "string","competition_name": "string","competition_type": "string","configured_at": 0,"demo_url": ["string"],"detailed_results": [{}],"faceit_url": "string","finished_at": 0,"game": "string","group": 0,"instances": [{}],"match_id": "string","organizer_id": "string","region": "string","results": {"score": {},"winner": "string"},"round": 0,"scheduled_at": 0,"started_at": 0,"status": "string","teams": {"property1": {},"property2": {}},"version": 0,"voting": null}`

## [tag/Matches/operation/getMatchStats](https://docs.faceit.com/docs/data-api/data/#tag/Matches/operation/getMatchStats)Retrieve statistics of a match

Retrieve statistics of a match

##### Authorizations:

*key*

##### path Parameters

| match_idrequired | string The id of the match |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"rounds": [{}]}`

# [tag/Matchmakings](https://docs.faceit.com/docs/data-api/data/#tag/Matchmakings)Matchmakings

## [tag/Matchmakings/operation/getMatchmaking](https://docs.faceit.com/docs/data-api/data/#tag/Matchmakings/operation/getMatchmaking)Retrieve details of a matchmaking of a game on FACEIT

Retrieve details of a matchmaking of a game on FACEIT

##### Authorizations:

*key*

##### path Parameters

| matchmaking_idrequired | string The id of the matchmaking |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"game": "string","icon": "string","id": "string","league_id": "string","long_description": "string","name": "string","queues": [{}],"region": "string","short_description": "string"}`

# [tag/Organizers](https://docs.faceit.com/docs/data-api/data/#tag/Organizers)Organizers

## [tag/Organizers/operation/getOrganizerByName](https://docs.faceit.com/docs/data-api/data/#tag/Organizers/operation/getOrganizerByName)Retrieve organizer details from name

Retrieve organizer details from name

##### Authorizations:

*key*

##### query Parameters

| namerequired | string The name of the organizer |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"avatar": "string","cover": "string","description": "string","facebook": "string","faceit_url": "string","followers_count": 0,"name": "string","organizer_id": "string","twitch": "string","twitter": "string","type": "string","vk": "string","website": "string","youtube": "string"}`

## [tag/Organizers/operation/getOrganizer](https://docs.faceit.com/docs/data-api/data/#tag/Organizers/operation/getOrganizer)Retrieve organizer details

Retrieve organizer details

##### Authorizations:

*key*

##### path Parameters

| organizer_idrequired | string The id of the organizer |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"avatar": "string","cover": "string","description": "string","facebook": "string","faceit_url": "string","followers_count": 0,"name": "string","organizer_id": "string","twitch": "string","twitter": "string","type": "string","vk": "string","website": "string","youtube": "string"}`

## [tag/Organizers/operation/getOrganizerChampionships](https://docs.faceit.com/docs/data-api/data/#tag/Organizers/operation/getOrganizerChampionships)Retrieve all championships of an organizer

Retrieve all championships of an organizer

##### Authorizations:

*key*

##### path Parameters

| organizer_idrequired | string The id of the organizer |
| --- | --- |

##### query Parameters

| publishedOnly | boolean Default: true When set to true only published championships are retrieved |
| --- | --- |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |
| sort | string Sorting criteria for retrieved championships, supports +createdAt, -createdAt |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Organizers/operation/getOrganizerGames](https://docs.faceit.com/docs/data-api/data/#tag/Organizers/operation/getOrganizerGames)Retrieve all games an organizer is involved with

Retrieve all games an organizer is involved with

##### Authorizations:

*key*

##### path Parameters

| organizer_idrequired | string The id of the organizer |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Organizers/operation/getOrganizerHubs](https://docs.faceit.com/docs/data-api/data/#tag/Organizers/operation/getOrganizerHubs)Retrieve all hubs of an organizer

Retrieve all hubs of an organizer

##### Authorizations:

*key*

##### path Parameters

| organizer_idrequired | string The id of the organizer |
| --- | --- |

##### query Parameters

| offset | integer[ 0 .. 1000 ] Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 50 ] Default: 50 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Organizers/operation/getOrganizerTournaments](https://docs.faceit.com/docs/data-api/data/#tag/Organizers/operation/getOrganizerTournaments)Retrieve all tournaments of an organizer

Retrieve all tournaments of an organizer

##### Authorizations:

*key*

##### path Parameters

| organizer_idrequired | string The id of the organizer |
| --- | --- |

##### query Parameters

| type | string Enum: "past" "upcoming" Kind of tournament. Can be upcoming(default) or past |
| --- | --- |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

# [tag/Players](https://docs.faceit.com/docs/data-api/data/#tag/Players)Players

## [tag/Players/operation/getPlayerFromLookup](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayerFromLookup)Retrieve player details

Retrieve player details

##### Authorizations:

*key*

##### query Parameters

| nickname | string The nickname of the player on FACEIT |
| --- | --- |
| game | string A game on FACEIT |
| game_player_id | string The ID of a player on game's platform |

### Responses

### Response samples

- 200

Content type

application/json

`{"activated_at": "2019-08-24T14:15:22Z","avatar": "string","country": "string","cover_featured_image": "string","cover_image": "string","faceit_url": "string","friends_ids": ["string"],"games": {"property1": {},"property2": {}},"infractions": null,"membership_type": "string","memberships": ["string"],"new_steam_id": "string","nickname": "string","platforms": {"property1": "string","property2": "string"},"player_id": "string","settings": {"language": "string"},"steam_id_64": "string","steam_nickname": "string","verified": true}`

## [tag/Players/operation/getPlayer](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayer)Retrieve player details

Retrieve player details

##### Authorizations:

*key*

##### path Parameters

| player_idrequired | string The id of the player |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"activated_at": "2019-08-24T14:15:22Z","avatar": "string","country": "string","cover_featured_image": "string","cover_image": "string","faceit_url": "string","friends_ids": ["string"],"games": {"property1": {},"property2": {}},"infractions": null,"membership_type": "string","memberships": ["string"],"new_steam_id": "string","nickname": "string","platforms": {"property1": "string","property2": "string"},"player_id": "string","settings": {"language": "string"},"steam_id_64": "string","steam_nickname": "string","verified": true}`

## [tag/Players/operation/getPlayerBans](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayerBans)Retrieve all bans of a player

Retrieve all bans of a player

##### Authorizations:

*key*

##### path Parameters

| player_idrequired | string The id of the player |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Players/operation/getPlayerStats](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayerStats)Retrieve statistics of a player for a given amount of matches

Retrieve statistics of a player for a given amount of matches

##### Authorizations:

*key*

##### path Parameters

| player_idrequired | string The id of the player |
| --- | --- |
| game_idrequired | string Example: csgo A game on FACEIT |

##### query Parameters

| offset | integer[ 0 .. 200 ] Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |
| from | integer Used to filter the dataset by date (minimum). Expected value is date ("items.stats.Match Finished At") in epoch milliseconds. |
| to | integer Used to filter the dataset by date (maximum). Expected value is date ("items.stats.Match Finished At") in epoch milliseconds. |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Players/operation/getPlayerHistory](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayerHistory)Retrieve all matches of a player

Retrieve all matches of a player

##### Authorizations:

*key*

##### path Parameters

| player_idrequired | string The id of the player |
| --- | --- |

##### query Parameters

| gamerequired | string A game on FACEIT |
| --- | --- |
| from | integer>= 0 The timestamp (Unix time) as lower bound of the query. 1 month ago if not specified |
| to | integer>= 0 The timestamp (Unix time) as higher bound of the query. Current timestamp if not specified |
| offset | integer[ 0 .. 1000 ] Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"from": 0,"items": [{}],"start": 0,"to": 0}`

## [tag/Players/operation/getPlayerHubs](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayerHubs)Retrieve all hubs of a player

Retrieve all hubs of a player

##### Authorizations:

*key*

##### path Parameters

| player_idrequired | string The id of the player |
| --- | --- |

##### query Parameters

| offset | integer[ 0 .. 1000 ] Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 50 ] Default: 50 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Players/operation/getPlayerStats](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayerStats)Retrieve statistics of a player

Retrieve statistics of a player

##### Authorizations:

*key*

##### path Parameters

| player_idrequired | string The id of the player |
| --- | --- |
| game_idrequired | string A game on FACEIT |

### Responses

### Response samples

- 200

Content type

application/json

`{"game_id": "string","lifetime": {"property1": null,"property2": null},"player_id": "string","segments": [{}]}`

## [tag/Players/operation/getPlayerTeams](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayerTeams)Retrieve all teams of a player

Retrieve all teams of a player

##### Authorizations:

*key*

##### path Parameters

| player_idrequired | string The id of the player |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Players/operation/getPlayerTournaments](https://docs.faceit.com/docs/data-api/data/#tag/Players/operation/getPlayerTournaments)Retrieve all tournaments of a player

Retrieve all tournaments of a player

##### Authorizations:

*key*

##### path Parameters

| player_idrequired | string The id of the player |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

# [tag/Rankings](https://docs.faceit.com/docs/data-api/data/#tag/Rankings)Rankings

## [tag/Rankings/operation/getGlobalRanking](https://docs.faceit.com/docs/data-api/data/#tag/Rankings/operation/getGlobalRanking)Retrieve global ranking of a game

Retrieve global ranking of a game

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string The id of the game |
| --- | --- |
| regionrequired | string A region of a game |

##### query Parameters

| country | string A country code (ISO 3166-1) |
| --- | --- |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Rankings/operation/getPlayerRanking](https://docs.faceit.com/docs/data-api/data/#tag/Rankings/operation/getPlayerRanking)Retrieve user position in the global ranking of a game

Retrieve user position in the global ranking of a game

##### Authorizations:

*key*

##### path Parameters

| game_idrequired | string The id of the game |
| --- | --- |
| regionrequired | string A region of a game |
| player_idrequired | string The id of a player |

##### query Parameters

| country | string A country code (ISO 3166-1) |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"position": 0,"start": 0}`

# [tag/Search](https://docs.faceit.com/docs/data-api/data/#tag/Search)Search

## [tag/Search/operation/searchChampionships](https://docs.faceit.com/docs/data-api/data/#tag/Search/operation/searchChampionships)Search for championships

Search for championships

##### Authorizations:

*key*

##### query Parameters

| namerequired | string The name of a championship on FACEIT |
| --- | --- |
| game | string A game on FACEIT |
| region | string A region of the game |
| type | string Enum: "all" "upcoming" "ongoing" "past" Kind of competitions to return |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Search/operation/searchClans](https://docs.faceit.com/docs/data-api/data/#tag/Search/operation/searchClans)Search for clans

Search for clans

##### Authorizations:

*key*

##### query Parameters

| namerequired | string The name of a clan on FACEIT |
| --- | --- |
| game | string A game on FACEIT |
| region | string A region of the game |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Search/operation/searchHubs](https://docs.faceit.com/docs/data-api/data/#tag/Search/operation/searchHubs)Search for hubs

Search for hubs

##### Authorizations:

*key*

##### query Parameters

| namerequired | string The name of a hub on FACEIT |
| --- | --- |
| game | string A game on FACEIT |
| region | string A region of the game |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Search/operation/searchOrganizers](https://docs.faceit.com/docs/data-api/data/#tag/Search/operation/searchOrganizers)Search for organizers

Search for organizers

##### Authorizations:

*key*

##### query Parameters

| namerequired | string The name of a organizer on FACEIT |
| --- | --- |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Search/operation/searchPlayers](https://docs.faceit.com/docs/data-api/data/#tag/Search/operation/searchPlayers)Search for players

Search for players

##### Authorizations:

*key*

##### query Parameters

| nicknamerequired | string The nickname of a player on FACEIT |
| --- | --- |
| game | string A game on FACEIT |
| country | string A country code (ISO 3166-1) |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Search/operation/searchTeams](https://docs.faceit.com/docs/data-api/data/#tag/Search/operation/searchTeams)Search for teams

Search for teams

##### Authorizations:

*key*

##### query Parameters

| nicknamerequired | string The nickname of a team on FACEIT |
| --- | --- |
| game | string A game on FACEIT |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Search/operation/searchTournaments](https://docs.faceit.com/docs/data-api/data/#tag/Search/operation/searchTournaments)Search for tournaments

Search for tournaments

##### Authorizations:

*key*

##### query Parameters

| namerequired | string The name of a tournament on FACEIT |
| --- | --- |
| game | string A game on FACEIT |
| region | string A region of the game |
| type | string Enum: "all" "upcoming" "ongoing" "past" Kind of competitions to return |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

# [tag/Teams](https://docs.faceit.com/docs/data-api/data/#tag/Teams)Teams

## [tag/Teams/operation/getTeam](https://docs.faceit.com/docs/data-api/data/#tag/Teams/operation/getTeam)Retrieve team details

Retrieve team details

##### Authorizations:

*key*

##### path Parameters

| team_idrequired | string The id of the team |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"avatar": "https://avatar.jpg","chat_room_id": "team-6204037c-30e6-408b-8aaa-dd8219860b4b","cover_image": "https://cover-image.jpg","description": "Some description","facebook": "https://facebook/team","faceit_url": "https://www.faceit.com/{lang}/teams/6204037c-30e6-408b-8aaa-dd8219860b4b","game": "csgo","leader": "leader","members": [{}],"name": "name","nickname": "nickname","team_id": "6204037c-30e6-408b-8aaa-dd8219860b4b","team_type": "premade","twitter": "https://twitter/team","website": "https://team.website","youtube": "https://youtube.com/team"}`

## [tag/Teams/operation/getTeamStats](https://docs.faceit.com/docs/data-api/data/#tag/Teams/operation/getTeamStats)Retrieve statistics of a team

Retrieve statistics of a team

##### Authorizations:

*key*

##### path Parameters

| team_idrequired | string The id of the team |
| --- | --- |
| game_idrequired | string A game on FACEIT |

### Responses

### Response samples

- 200

Content type

application/json

`{"game_id": "string","lifetime": {"property1": null,"property2": null},"segments": [{}],"team_id": "string"}`

## [tag/Teams/operation/getTeamTournaments](https://docs.faceit.com/docs/data-api/data/#tag/Teams/operation/getTeamTournaments)Retrieve tournaments of a team

Retrieve tournaments of a team

##### Authorizations:

*key*

##### path Parameters

| team_idrequired | string The id of the team |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

# [tag/Tournaments](https://docs.faceit.com/docs/data-api/data/#tag/Tournaments)Tournaments

## [tag/Tournaments/operation/getTournamentsList](https://docs.faceit.com/docs/data-api/data/#tag/Tournaments/operation/getTournamentsList)Retrieve tournaments v1 (no longer used)

Retrieve tournaments v1 (no longer used). Please refer to the Championships controller to retrieve tournaments v2

##### Authorizations:

*key*

##### query Parameters

| game | string A game on FACEIT |
| --- | --- |
| region | string A region of the game |
| offset | integer>= 0 Default: 0 The starting item position |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Tournaments/operation/getTournament](https://docs.faceit.com/docs/data-api/data/#tag/Tournaments/operation/getTournament)Retrieve tournament details

Retrieve tournament details

##### Authorizations:

*key*

##### path Parameters

| tournament_idrequired | string The id of the tournament |
| --- | --- |

##### query Parameters

| expanded | Array ofstringsunique Items Enum: "organizer" "game" List of entity names to expand in request |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"anticheat_required": true,"best_of": null,"calculate_elo": true,"competition_id": "string","cover_image": "string","custom": true,"description": "string","faceit_url": "string","featured_image": "string","game_data": {"assets": {},"game_id": "string","long_label": "string","order": 0,"parent_game_id": "string","platforms": [],"regions": [],"short_label": "string"},"game_id": "string","invite_type": "string","match_type": "string","max_skill": 0,"membership_type": "string","min_skill": 0,"name": "string","number_of_players": 0,"number_of_players_checkedin": 0,"number_of_players_joined": 0,"number_of_players_participants": 0,"organizer_data": {"avatar": "string","cover": "string","description": "string","facebook": "string","faceit_url": "string","followers_count": 0,"name": "string","organizer_id": "string","twitch": "string","twitter": "string","type": "string","vk": "string","website": "string","youtube": "string"},"organizer_id": "string","prize_type": "string","region": "string","rounds": [null],"rule": "string","started_at": 0,"status": "string","substitutes_allowed": 0,"substitutions_allowed": 0,"team_size": 0,"total_prize": null,"tournament_id": "string","voting": null,"whitelist_countries": ["string"]}`

## [tag/Tournaments/operation/getTournamentBrackets](https://docs.faceit.com/docs/data-api/data/#tag/Tournaments/operation/getTournamentBrackets)Retrieve brackets of a tournament

Retrieve brackets of a tournament

##### Authorizations:

*key*

##### path Parameters

| tournament_idrequired | string The id of the tournament |
| --- | --- |

### Responses

### Response samples

- 200

Content type

application/json

`{"game": "string","matches": [{}],"name": "string","rounds": [{}],"status": "string"}`

## [tag/Tournaments/operation/getTournamentMatches](https://docs.faceit.com/docs/data-api/data/#tag/Tournaments/operation/getTournamentMatches)Retrieve all matches of a tournament

Retrieve all matches of a tournament

##### Authorizations:

*key*

##### path Parameters

| tournament_idrequired | string The id of the tournament |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"end": 0,"items": [{}],"start": 0}`

## [tag/Tournaments/operation/getTournamentTeams](https://docs.faceit.com/docs/data-api/data/#tag/Tournaments/operation/getTournamentTeams)Retrieve all teams of a tournament

Retrieve all teams of a tournament

##### Authorizations:

*key*

##### path Parameters

| tournament_idrequired | string The id of the tournament |
| --- | --- |

##### query Parameters

| offset | integer>= 0 Default: 0 The starting item position |
| --- | --- |
| limit | integer[ 1 .. 100 ] Default: 20 The number of items to return |

### Responses

### Response samples

- 200

Content type

application/json

`{"checked_in": [{}],"finished": [{}],"joined": [{}],"started": [{}]}`