
# Introduction

# None

# Some

# Many

# Few

# Alternate

This task was solved using a Depth First Search algorithm. Starting at node s, we look at each neighbor, checking if it is not already visited and if its color is alternate from s. If this is the case, then we check if the node is t, in which case we return true. If the node is not t, then we call traverse recursively with the neighbor as s and do the same steps. If a recursive call returns true, then we break, returning true all the way up the chain to the initial call of the traverse function.