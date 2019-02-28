Because I got two separate feedbacks (via email & via linkedin) and some of the content is touching same points, I've organized my response in bullet points, quoting the original feedback. I will try to keep it short and informative.

# General comments

> "we are looking for senior people to go beyond the simple exercise"

> "I told him and also to you we look for the underlying challenges to be at least mention."

I must start with the above, because I think this is the root of miunderstanding. I don't recall hearing the above at all. If anything - after having short conversation about the exercise -  the impression during the call that I got was that the exercise is short. That it should be completed in short period of time and focus is mainly on the contextual separation (a.k.a abstraction).

My understanding was (this was not stated, this is my impression) that given limited time (beside being correct), the exercise will check

* how much I can provide
* is the code maintainable
* is it easy to add features in
* is the code well decoupled

When I'm doing this kind of exercises I always try to adhere to the explicit requests in the descriptions. I've been informed few times in the past that I've explored too much, that I should keep the solution YAGNI, because I was doing things that were reserved for the actual interview. Since then I tend to point the potential improvemements that seem obovious - those kind of improvements I would do straight away if it was main normal day-time task. Level of seniority can be recognized in a person, when I he looks at the problem beyond for what he was asked for.
Thus though I recognized things like a need for a cache, I've made a comment about it (that this will be trivial), but did not implement that because I was not explicitly ask for (though every inch in my body was screaming that I should).

> "In general what i value mostly is the journey from 'problem --> solution'"

I agree. That's why when I code I value fine-grained commits, with messages explaining steps and motivation for next change. I thought that was well expressed in the `git log`.

> "We would have expected more explanations, comments and comparisons in the README. "

That was never stated in the exercies. Doesn't code speak for itself?

> "No tests (this is a deal breaker)"

> "and i believe the coding exercise explicitly asks for them"

As I can agree that I've should have added tests to the solution, I want to clarify that this was never explicitly stated in the task (as the above comment suggests).

> "the solution needs to fit the problem - be well tested - and easily maintainable"

> "the solution seems to focus on functional development rather than tackling the business problem definition, as a team we are not always 100% functional - but quite pragmatic in our approach"

I agree test were not aded, but soluton is testable. Please note that no criticial changes were made to the design to write both unit and integration tests.
The solution is maintainable: please see the cache comment explored below.

Tagless Final, the FP approach was not the goal of its own. It never is! The goal (and I can not strees this to strongly) is to have maintainable code that has small probablility of introducing bugs.

Level of abstraction will minimalize potenatial erros as each layer of abstraction is using minimal set functionality that it needs to get's it job done. This reduces dramatically overall set of possible solutions (including the wrong ones) at the same giving code that easy to extend.

# Technical comments

> "No cache for the exchange data no mention of it at all"
> "Why is a request always going to the 3rd party? What if we had to pay per requests? Why not cache the data? These are points we expect a senior person to touch upon."

First of all, I must disagree with `no mention of it at all` - it was mentioned in the readme.
I've explained already why cache was not encoded (please see first paragraph).

But please just let me point something very important: given the way solution was encoded, commit that introduced caching (`b6495c4`) is trivial! It's something around 10 lines of code added. This speaks against the "overengineered" solution. The architecture of the solution is well prepared to adjust well to incoming changes to the requirements.

This one of the main reasons why it was encoded that way. Noone is exploring limits of HKT in Scala for just the pleasure of doing so :)

> "Despite having things organized with Tagless Final, some obvious dependencies are not really made available via it (e.g. Http Client, instead passing around the ExecutionContext)"

During the introduction of unit test, instance of `Excahnge` extracted part of its code to a seprate typeclass - but that's an implemetation detail. I don't quite understand the ExecutionContext remark. The execution context is required by underlaying implementation, this is how http4s client works (I can agree that is unfortuante since it clearly should not need that at that level) - but we can only go as far as the tools we are using
..

> "Amounts as Double"

> "It would have taken less time than writing then comment about it the README"

Commit `943652a` shows that it was not that trivial. But I agree, this should be part of the solution.

> "Newtypes: why are they better than value classes? And what are some drawbacks?"

> "why newtypes better than value classes for example ?"

Newtype used is directly inspired from Haskell. Underneath it generate a value class (`AnyVal`), but that's not its nly feature. One of the main benefits is dynamic type class deriviation.
Why in the exercise we can compare amounts with `<`?

```
if (amount < Amount.ZERO) ...
```

Because there is (in cats) and `Order[BigDecimal]`, and since `Amount` wrapps the `BigDecimal` we get `Order[Amount]` for free! If that's not a selling point, then I don't know what is :)

TL;DR Same assembly (see `javap`), richer functionality.

What are the drawbacks? Current version is missing an `unapply` which is troubeling but I think they have a ticket for that.







