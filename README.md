# Business Model #

When advertisement bannners are displayed to users in mobile application(app_id) in country(country code) from advertiser(advertiser_id) ```impression``` events was recorded and stored. After that user could click to banner and in this case ```click``` event is recorded.

# Input

## Parameters

- application should accept list of files with clicks and impression events. There could be up to 200 files with each event type

## Impression event (some banner was displayed to users)

- id
- app_id
- country_code 
- advertiser_id


[Data](https://gist.github.com/ztanis/9088a05b28866c5469d6592b5ae94c5b)

## Click event (user_clicked to banner)

- impression_id
- revenue

[Data](https://gist.github.com/ztanis/dd9fac886279b5a3021e1b36fe2bde43)

# Objectives

## 1. Read events stored in JSON files

There are 4 json files per each event types.

## 2. Group by dimensions and calculate metrics

Dimensions(data is being grouped by those fields):

- app_id
- country_code

Metrics: 

- impressions: sum of impressions
- clicks: sum of clicks
- revenue: sum of revenue

Please write output to JSON file in the following format:
```
[{"app_id": 1, "country_code": "US", "impressions": 102, "clicks": 12, "revenue"}]
```

## 3. Make a recommendation for top 5 advertiser_ids to display for app/country combination.

Fields:

- app_id
- country_code
- recomended_advertiser_ids - list of top 5 advertisers based on highest rate of "revenue" / "impressions" metrics. To top5 recommended_advertiser_ids should be included advertiser_ids with a highests revenue per impression rates.

Please write output to JSON file in the following format:

```
[{"app_id": 1, "country_code": "US", "recommended_advertiser_ids": [32, 12, 45, 4, 1]}]
```

## Tech stack

- Accomplish task in Scala programming language
- Don't use any data processing framework(like Spark, Flink, ..) for first 2 objectives(`1. Read events stored in JSON files`, `2. Group by dimensions and calculate metrics`)
- Application will be run on 1 instance with 8 cores.