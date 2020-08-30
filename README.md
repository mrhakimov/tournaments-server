# tournaments-server
A web service (HTTP REST API) to organize tournaments.

### Summary
**Stack:** Java, Spring Boot, PostgreSQL, REST API, Maven

Designed REST API provides interface to create, get and start (including cases where the number of participants is odd) tournaments by identifier.
It is also possible to add and delete participants from tournament they associated, and match summarization (by providing scores of participants of match).

Also some additional methods provided to hold, update and delete tournaments, to update and get list of participants, etc..

`Collections.shuffle()` on the list of participants used to generate match grid. At all, match grid is generated layer by layer, which means that grid, for example, for half-finals will only be generated after all matches from quarter-finals summarized. If the number of participants is odd, one of them will skip current layer and will automatically proceed to the next layer.

### What can be done better?
1. Tests. I should cover more cases.
2. API Design? Maybe idea with `/tournaments/start/{tournamentId}` is not that good.
3. Validators.
4. Commits. I was planning to create another repo for this project. Firstly, this one was created just for test of something, doesn't matter.
5. Screenshots :)
(Telegram zipped them)

### Screenshots
Getting tournament with incorrect id:
![](/screenshots/1.jpg)

Creating tournament with incorrent max number of participants:
![](/screenshots/2.jpg)

Creating correct tournament:
![](/screenshots/3.jpg)

Getting participants of newly created tournament:
![](/screenshots/4.jpg)

Adding participants to tournament:
![](/screenshots/5.jpg)

Getting participants of filled tournament:
![](/screenshots/6.jpg)

Deleting participant from tournament:
![](/screenshots/7.jpg)

Starting tournament:
![](/screenshots/8.jpg)

Participants' table:
![](/screenshots/9.jpg)

Matches' table:
![](/screenshots/10.jpg)

Tournaments' table:
![](/screenshots/11.jpg)

Summarizing matches:
![](/screenshots/12.jpg)

Trying to summarize match with equal scores of participants:
![](/screenshots/13.jpg)
