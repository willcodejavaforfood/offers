I'm normally a Controller/Service/Persistence layer kind of guy, but to keep it simple, and to reduce the test
duplication, I decided to omit the service layer for this task. There was not much business logic to start with but it
meant that the repository and controller had to do more than what they normally would.

With only six classes I choose to not create any further packages, but I would normally have at least a package for each
layer.

I am a firm beleiver in the value of end to end tests so instead of unit testing my controller I made that the end to
end test. The Offer and Repository are properly unit tested and I hope you agree with that being enough to show I
understand how to write a good unit test.