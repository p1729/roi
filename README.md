# ROI user-n-photos API
Load facebook user's photo data and their reaction

## Design Overview
1. DB Relations
    ```
   User (Many) ---tag-- (Many) Photo
   Photo (One) -----(One) Album
   Photo (One) ------(Many) Reaction
   ```
2. When user sends a load data request, that same request may spawn many calls to FB and can have huge data to load which may take longer time to persist. So, we can the accept the request by checking token permission and trying to load users basic data before loading the photos data in async. Also, send the Location header mentioning the URL to get the loaded data. `TODO` We can also send the state using some 'Content-Location' header as well 
3. We can relay the errors while accessing FB API in case of invalid access token, session expire, wrong fbid, and in case of `TODO` errors like 503 we can retry
4. Deletion will not delete the photo as photo may till every user tagged is removed, same is the case with photo and album. 

### TODO
1. HATEOS
2. Exception handling, retries and sorting as mentioned above
3. Tests

## API Documentation
> Try out these at [https://p1729-roi.herokuapp.com/](https://p1729-roi.herokuapp.com/)

1. Send request to load facebook user-n-photo data asynchronously
    ```
    POST /users
    {
        "fbId": "user's facebook id (not username)"
        "accessToken": "users's facebook access token with required permissions(email, user_photos, user_gender)"
    }

    ```
    Sends API response `202` in case request is accepted and sends the resource location in `Location` header, `400` in case wrong user id or lack of required permissions etc., `409` in case of duplicate request
    
 2. Fetch the loaded user's data
    ```
    GET /users/{id}
    ``` 
    Sends `200` with below model and `404` if not found
    ```json
    {
      "name": "string",
      "gender": "string",
      "email": "string",
      "profile_pic_url": "string"
    }
    ```
 3. Fetch user's photo data with reaction summary
    ```
    GET /users/{id}/photos 
    ```
    Sends `200` with below model and `404` if not found
    ```json
    {
      "photos": [
        {
          "link": "string",
          "fb_link": "string",
          "album_name": "string",
          "reactions": [
            {
              "type": "string",
              "total_count": "string"
            }     
          ]
        } 
      ]
    }
    ```
 4. Delete loaded user's data
    ```
    DELETE /users/{id}
    ```
    Sends `200` or `404` if not found
    
## Technology Stack, DB and Frameworks used
Spring boot, Spring MVC, Maven, Java 8, Liquibase, Hibernate, Lombok, H2