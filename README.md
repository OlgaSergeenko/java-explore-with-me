# Explore With Me project

## Short description of the main functionality:

- Public user API : serves to search for events, event compilations, and event categories by selected filters. 
It also allows to obtain the full information about the particular event as well as number of views for it.  

- Registered user API : allows to add events to the platform, as well as to modify, cancel and view. There is an 
opportunity to apply for participation in events. Event creator is able to confirm requests submitted 
by other users.

- Admin API : allows to add, modify and delete categories for events, as well as add, delete and pin on the main 
page compilations of events. It allows events moderation - publication or rejection, and user 
management - adding, viewing and deleting.

## Full description of additional feature - COMMENTS
To see pull request for feature use this [link](https://github.com/OlgaSergeenko/java-explore-with-me/pull/3)

Registered users may leave comments to the events. Comments can be edited by the author.
Event initiator may reply to the comments for his own events. Once the initiator has replied to the comment the original
comment can not be edited.

#### POST/users/{userId}/events/{eventId}/comments 
Endpoint to post new comment. Any registered user may leave comment to the event.
Event should be in PUBLISHED state to be able to leave a comment.

#### POST/users/{userId}/events/{eventId}/comments/{commentId}/reply
Endpoint to reply to comment. Only event initiator can reply to the comments (leaved to his events).
There is only one reply can be posted to the comment.

#### PATCH/users/{userId}/events/{eventId}/comments/{commentId}
Endpoint to modify the comment. Only comment author can edit the comment.
Comment can be modified only before the reply to this comment is posted by the event initiator.

#### GET/users/{userId}/events/{eventId}/comments
Endpoint to review all the comments (with event initiator replies if there is any) leaved to the particular event.
All registered users may review all comments.
Pagination is available at this endpoint.

#### DELETE/users/{userId}/events/{eventId}/comments/{commentId}
Endpoint to remove comment. Only comment author (or admin) may remove comment.
Only comment with no event initiator reply can be removed.
The event owner respond to the comment can not be removed after being posted.

#### GET/admin/events/{eventId}/comments
Admin endpoint to review all the comments (with event initiator replies if there is any) leaved to the particular event.
Pagination is available at this endpoint.

#### DELETE/admin/events/{eventId}/comments/{commentId}
Admin endpoint to remove the comment. Admin is allowed to remove any comment and comment respond. Once the comment is 
removed the response (if there is any) should be removed as well. 

### Project schema : 
[PROJECT STRUCTURE](EWM%20project.jpg)
![STRUCTURE](EWM%20project.jpg)

### Database diagram : 
[DATABASE DIAGRAM](DB%20schema.jpg)
![DIAGRAM](DB%20schema.jpg)

