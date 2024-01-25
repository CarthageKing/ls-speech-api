# Documentation

## Introduction

This is a short documentation about the service. The service manages speech information of users. It has the following functionalities:

- Create a speech. A speech requires all of the following to be provided to be considered a speech:
  - set of authors
  - set of keywords about the speech
  - speech text itself
  - speech date
  - id (**NOT** required. will be created by the system to uniquely identify the speech record)
- Get a speech using its ID
- Do partial updates on any part of the speech. The following can be updated individually:
  - set of authors
  - set of keywords about the speech
  - speech text itself
  - speech date
- Delete a speech using its ID
- Search for speeches matching a given set of criteria. More on this below

The structure of a speech record is defined as a JSON record with the following properties:

```json
{
	"id": "string with max of 64 characters",
	"speechDate": "date string with the format of yyyy-MM-dd",
	"authors": [ "string array. nulls or complete blank strings are not allowed. each value has a max of 1024 characters" ],
	"keywords": [ "string array. nulls or complete blank strings are not allowed. each value has a max of 1024 characters" ],
	"speechText": "string. nulls or complete blanks are not allowed. max of 1048576 characters"
}
```

Below next sections describe each API. For illustration purposes, the example requests below assume the application is running on `localhost` and listening on port `9091`.

### Create Speech API

Create a speech record. All the properties except ID has the be provided.

Request:
```sh
curl --location 'http://localhost:9091/speeches/o' \
--header 'Content-Type: application/json' \
--data '{
	"speechDate": "2011-12-13",
	"authors": ["Boston Journal", "Linda Johnson"],
	"keywords": ["fox", "lazy dog"],
	"speechText": "The quick brown fox jumps over the lazy dog."
}'
```
Example Successful Response:
```json
{
    "header": {
        "statusCode": "201",
        "statusMessage": "Speech data created"
    },
    "data": {
        "id": "fdbde203-1e09-4f49-b2fd-58d6726f1d2b",
        "speechDate": "2011-12-13",
        ... other details ...
    }
}
```

### Get A Speech By ID

Gets the speech record identified by the given ID.

Request:
```sh
curl --location 'http://localhost:9091/speeches/o/fdbde203-1e09-4f49-b2fd-58d6726f1d2b'
```
Example Successful Response:
```json
{
    "header": {
        "statusCode": "200",
        "statusMessage": "Operation successful"
    },
    "data": {
        "id": "fdbde203-1e09-4f49-b2fd-58d6726f1d2b",
        "speechDate": "2011-12-13",
        ... other details ...
    }
}
```

## Challenges Encountered and Solutions/Workarounds Provided (If Any)

## Other General Limitations

- Security is not implemented. Anyone can create speeches. Anybody can retrieve, update, search and delete everyone else's speeches.
- Inefficient querying on text fields due to use of `like` (i.e. contains string) queries. Explore use of Hibernate Search with Lucene in the future.
- Only tested with H2 database. A more proper database like Postgres could work and [trigram text indexes](https://about.gitlab.com/blog/2016/03/18/fast-search-using-postgresql-trigram-indexes/) can be used there to improve search performance.
- Not tested with 'big' data. No performance tests done. Tests mainly focus on functionality.
- Currently only tested on Linux and Windows.
- Only tested with English data and test data. No actual speech data used.
