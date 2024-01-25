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

### Delete Speech By ID

Deletes the speech record with the given ID. The deleted record is returned in the response.

Request:
```sh
curl --location --request DELETE 'http://localhost:9091/speeches/o/fdbde203-1e09-4f49-b2fd-58d6726f1d2b'
```
Example Successful Response:
```json
{
    "header": {
        "statusCode": "200",
        "statusMessage": "Speech was deleted"
    },
    "data": {
        "id": "fdbde203-1e09-4f49-b2fd-58d6726f1d2b",
        "speechDate": "2011-12-13",
        ... other details ...
    }
}
```

### Partial Update of Speech

Do updates on portions (or all except ID) of the speech record. The fully updated record is returned in the response.

Request (only updates the `speechDate` property):
```sh
curl --location --request PATCH 'http://localhost:9091/speeches/o/5cd1964d-29f5-43d6-913b-821b9f14b7a9' \
--header 'Content-Type: application/json' \
--data '{
	"speechDate": "2001-12-13"
}'
```
Example Successful Response:
```json
{
    "header": {
        "statusCode": "200",
        "statusMessage": "Speech was updated"
    },
    "data": {
        "id": "5cd1964d-29f5-43d6-913b-821b9f14b7a9",
        "speechDate": "2001-12-13",
        ... other details ...
    }
}
```

### Speech Search API

Search for speeches matching the given set of criteria. If no criteria provided, returns everything. The results are not paginated.

Basic Request (returns all):
```sh
curl --location 'http://localhost:9091/speeches/_search'
```
Example Successful Response:
```json
{
    "header": {
        "statusCode": "200",
        "statusMessage": "Operation successful"
    },
    "data": {
        "totalRecords": 1,
        "entries": [
            {
                "id": "5cd1964d-29f5-43d6-913b-821b9f14b7a9",
                "speechDate": "2001-12-13",
                ... other details ...
            }
        ]
    }
}
```

Here are the rules for the search API:

- Currently, can only use the following search parameters:
  - `authors`: search for speeches whose authors' names might contain the given character sequence. This is a case-insensitive search and is punctuation-aware. Multiple values within one search parameter indicate to search speeches that might contain any of the indicated character sequences. Multiple values must be separated by `|` character. Examples:
    - `?authors=val` will retrieve a speech authored by `Val Kilmer` and another one authored by `The Oval Office`
    - `?authors=val|tom` will include all the results from the previous bullet point plus speeches authored by `Atom Ant`
    - A speech authored by `Arthur C. Clarke` will be found by a query `?authors=arthur c. clarke` but not by `?authors=arthur c clarke`
  - `keywords`: search for speeches whose keywords might contain the given character sequence. The search rules are the same as for `authors` but this search looks at the speech's `keywords` section
  - `snippetsOfTexts`: search for speeches whose speech data might contain the given character sequence. The search rules are the same as for `authors` but this search looks at the speech's `speechText` section
  - `dateRangeFrom`: if provided, then retrieves only records whose `speechDate` property is greater than or equal to the given range
  - `dateRangeTo`: if provided, then retrieves only records whose `speechDate` property is less than or equal to the given range
- If both `dateRangeFrom` and `dateRangeTo` are provided, then a check is made to ensure that `dateRangeFrom <= dateRangeTo`. Otherwise an exception is thrown and a HTTP Bad Request is sent back to the caller of the API
- It is currently not possible to specify multiple instances of the same parameter in order to perform an **AND** search. For example, it is currently not possible to search for speeches authored by both `nelson` and `vedel` since that would require to formulate the query to `?authors=nelson&authors=vedel`, which is not supported

Example using all the search parameters:
```sh
curl --location 'http://localhost:9091/speeches/_search?dateRangeFrom=2000-01-01&dateRangeTo=2003-01-01&authors=journal&keywords=dog&snippetsOfTexts=jumps%7cquick'
```

## Challenges Encountered and Solutions/Workarounds Provided (If Any)

- Attempted to use Hibernate's `@OneToMany` and `@ManyToOne` annotations to express relationships between the different entities and let Hibernate cascade the updates/deletes/etc. Issues were encountered that resulted in just decoupling the entities and handling the create/update/delete cascade logic in the application. Issues were:
  - Unpredictability in the amount and number of SQL statements generated and executed. That approach was generating more SQL that the current approach
  - Generation of random foreign key name and no easy way to override it
- Unfamiliarity with `mapstruct` led to sparse use in the code for just the simplest of mappings (i.e. speech to speech entity and vice versa)
- During requirements gathering, it was mentioned that there was no limit to the lengths of the  author names, keywords and speech data. Initially, this was modeled as `clob` columns in H2. However, issues encountered in using these columns arose which resulted in the current implementation imposing a limit to the lengths of these columns in such a way that Hibernate wouldn't convert them to `clob` columns. The issues were:
  - Cannot do a `lower()` function against the `clob` columns, necessary for doing the case-insensitive search
  - Cannot make `clob` columns part of the primary key

## Other General Limitations

- Security is not implemented. Anyone can create speeches. Anybody can retrieve, update, search and delete everyone else's speeches.
- Inefficient querying on text fields due to use of `like` (i.e. contains string) queries. Explore use of Hibernate Search with Lucene in the future.
- Only tested with H2 database. A more proper database like Postgres could work and [trigram text indexes](https://about.gitlab.com/blog/2016/03/18/fast-search-using-postgresql-trigram-indexes/) can be used there to improve search performance.
- Not tested with 'big' data. No performance tests done. Tests mainly focus on functionality.
- Currently only tested on Linux and Windows.
- Only tested with English data and test data. No actual speech data used.
