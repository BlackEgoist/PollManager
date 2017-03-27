A standalone web-server app with the following functionality is considred:
1) Creating new polls,
2)Starting a new poll and generate a link to it,
3)Closing an existing poll,
4)Showing statistics about about chosen poll,
5)Registering a vote for certain option in a poll.

This functionality has been implemented as follows:

1-3) PollController @Scheduled method managePolls() calls for createMyPoll()
method in order to instantiate a new Poll object and starts the poll by adding
it the the list of active polls.
At the start of the app ACTIVE_POLLS_NUMBER polls are created. After that every
DELAY_MILLS milliseconds this method closes the oldest active poll, adds it to
the list of closed polls and, after that, starts a new poll.

createMyPoll() method create a new poll object with number of options ranging between
MIN_OPTION_NUMBER and (MIN_OPTION_NUMBER + OPTION_RANGE - 1). Every created poll has a unique id.

Link to the poll can be obtained via GET query method using the path "/api/polls". Using this call
will call getPollsList() method returning a JSON object with two arrays of JSON objects: active and closed.
Every array contains a number of poll titles with corresponding generated path links for the poll.

4) Quering a certain poll via provided link with GET query (e.g. /api/polls/{id}) will call getPollInfo() method
which returns a JSON object containig full poll info such as poll id, title, description, and array of
poll options, each of it a JSON object with title and votes counter.

5) Quering a certain poll via provided link with POST query and option title parameter as a requiered @RequestParam
will register a vote for chosen option on condition that this option exists and a poll is still active.
The vote() method of PollController managing this operation will return a JSON object of poll, containing full
info about the poll with incremented counter for the option chosen as a @RequestParam in case of correct query.
In case of missing parameter, a BAD_REQUEST return is guaranteed.


