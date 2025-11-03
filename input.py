import sys
import random as r

n = int(sys.argv[1])
vertices = list(range(n))
s, t = r.sample(vertices, 2)
red = []

adj_list = [[] for _ in range(n)]
total_edges = 0

for i in range(n):
    if r.randint(0, 4) == 4:
        red.append(i)
    num_edges = r.randint(0, n // 3)
    possible_targets = list(range(i + 1, n))
    
    if possible_targets:
        selected = r.sample(possible_targets, min(num_edges, len(possible_targets)))
        for j in selected:
            adj_list[i].append(j)
            adj_list[j].append(i)  # Undirected graph
            total_edges += 1

print(f"{n} {total_edges}")
print(f"s={s}, t={t}")
print(" ".join(map(str, red)))
print("\n")
for edges in adj_list:
    print(" ".join(map(str, sorted(edges))))
