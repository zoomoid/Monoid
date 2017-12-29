# Monoid
Monoid is a software synthesizer written in Java with Beads for educational purposes

# About 'Monoid'

Monoid features modularity for both subtractive as well as additive synthesis. Its core parts are oscillators and filters,
ordered and managed by controllers which allow routing to be made individually. Additionally, processing through effects
can be done in post by routing a bus to the effects controller.

Monoid implements and uses the open-source package Beads (<http://beadsproject.net/>) as backend audio driver for Java.
Since Beads brings a lot of features and abilities with it in the first place, the UGen structure inherited from the library
is used throughout Monoid and is used to extend the synthesizer with useful features like effects or filters.

Signal flow is quite intuitive and can be illustrated like this:
