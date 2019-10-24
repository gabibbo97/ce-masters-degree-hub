## k-degree anonymity

### Install requirements

```
pip install -r requirements.txt
```

### Usage

```
usage: python k-degree.py k_value graph_to_anonymize.csv
```

#### Example

```
usage: python k-degree.py 3 Dataset/graph_friend_100_10_100.csv
```

#### Dataset

In Dataset directory you can find 4 datasets:

- Dataset\graph_friend_100_10_100.csv
- Dataset\graph_friend_1000_10_100.csv
- Dataset\graph_friend_10000_10_100.csv
- Dataset\graph_friend_10000_100_1000.csv

Where the first number is the number of nodes, instead, the second and the third number is the min and max link for each node.

Each line inside `.csv` file is an adjacency list.
