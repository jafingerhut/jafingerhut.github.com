# Why doesn't Clojurians Slack let me search through old messages?

Someone created the Slack "community" (TBD the correct Slack
terminology for this) https://clojurians.slack.com circa 2015.  By
about 2017, it was probably the most popular on-line chat location for
topics related to Clojure development.

This Slack community was created via a free plan provided by Slack for
open source communities.  The advantage is that Slack pays for all of
the resources.  A disadvantage is that the free service limits the
searchable message history to the most recent 10,000 messages, total
across all channels.


# Searching the existing logs of Clojurians Slack messages

Let me know if you have better ways than described here.

## clojurians-log

Browse clojurians-log: https://clojurians-log.clojureverse.org

On Google, you can limit searches to any web site by appending
`site:site-url` to the end of the search, e.g. to search for
occurrences of `defmacro` only on the Clojurians log site, use the
Google search:

```
defmacro site:clojurians-log.clojureverse.org
```

## Clojurians Zulip

At least in the web browser UI for Zulip, after joining you should see
a wide text box near the top center of the page, with a magnifying
glass icon on its right side.  Click on that magnifying glass icon, or
in the text box, and type in your search terms, and press
return/enter.

As of 2020, most of the messages on Clojurians Zulip are in the
`slack-archive` stream, with a separate topic (TBD the correct Zulip
terminology here) for each Clojurians Slack channel that is mirrored.
Very likely most of your search results will appear in one of those.

TBD how to limit the search to a single Zulip topic.  Zulip's help
page on advanced search is probably a good place to read about more
features: https://clojurians.zulipchat.com/help/search-for-messages


# How to start logging a Clojurians Slack channel

Logging of messages on Clojurians Slack is enabled on a
per-Slack-channel basis, not globally.

First, join the Clojurians Slack channel yourself.  Then send one or
more of these messages to the channel:

+ For Clojurians log: `/invite @logbot`
+ For logging to Clojurians ZulipChat: `/invite @zulip-mirror-bot`

If someone has already successfully invited one of these bots before,
you should see a response like `@logbot is already in this channel`,
and no other human member will see any activity on the channel.

If yours is the first attempt to add one of these bots, then everyone
on the channel including you should see a message like `logbot [APP]
was added to #channel-name`.


# Can we upgrade Clojurians Slack to a paid version?

Yes, someone could choose to pay for this.  If someone chose to do
this, they would be legally responsible to pay the bill to Slack for
_all_ users of Clojurians Slack.  The current pricing as of 2020 puts
the bill at somewhere near USD $1,200 per month.  This amount could
change later based upon Slack's pricing model, but it could also go up
or down as the number of active users of Clojurians Slack changes.
Slack does not provide a way to bill every user of Clojurians Slack
separately for an equal fraction of that amount.

Given this price, it is perhaps quite understandable that no one has
volunteered to take on such a financial obligation themselves.

Several people have asked whether Slack offers an option where:

+ Individual users could pay a few dollars per month so that _they_
  had access to unlimited search history on Clojurians.slack.com, but
+ everyone else who used it for free would continue to have the
  current limited search history.

As of 2020, Slack does not offer such an option, and they expressed no
interest in doing so.


# Why doesn't everyone switch over to something better?

First, there are many dimensions and aspects to "better", not a single
0 to 10 ranking that captures all judgements of a service that all
people value equally.  Slack has many good qualities.

There are already on-line communities on other services created for
discussing Clojure, e.g. on IRC, Discord, ZulipChat, and several
others.  As of 2021, none of them have reached the popularity that
Slack has.

+ Discord:
  https://www.reddit.com/r/Clojure/comments/6c4z91/a_discord_for_clojurians/
+ Zulip: https://clojureverse.org/t/ann-searchable-slack-archive/3777
+ IRC and some others listed here: https://clojure.org/community/resources

Why doesn't everyone just switch to some other service?

Every individual chooses which of these services they want to use.
They are not limited to one, but few people want to participate in 10
of them simultaneously.  Even if someone is aware of all of them, they
are likely to choose one, or perhaps two or three, that they find to
give them a good UI experience, and more importantly, where _others
respond to their questions_.  So far, Slack has been the most popular
place for Clojure developers.  Yes, there is a network effect here:
https://en.wikipedia.org/wiki/Network_effect

Could the current situation ever change?  Of course it could.  But
there is no "Clojure dictator for on-line chat" that can make all of
these individuals use a different service.  Think like a politician in
a free society, or a sales person, not like a monarch issuing decrees,
and you might find a way to persuade many people to switch services.
Keep this in mind: merely because _you_ think service X is clearly
superior to Slack, that fact alone is unlikely to convince people to
join service X and stop using Slack.  It is still unlikely, even if
you have a well written article detailing all the ways that X is
better than Slack.  Even if you find many people who agree with your
article.

IRC was the most popular on-line chat forum for Clojure for several
years, and then the majority of people gradually changed their primary
choice to Slack.  No one forced everyone to switch, and IRC is
probably still used by at least a few people for Clojure discussion.

The Clojurians Slack channel #community-development is the most
appropriate place on Slack to discuss this.  It has been discussed
several times per year for N years, so it is not easy to come up with
a novel idea in this area.