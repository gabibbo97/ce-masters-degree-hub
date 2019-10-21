# Question

Can the similarity attack be formalized as an high correlation between elements in a q\*-block with their QI?

## Relation vulnerable to similarity attack

$$
  K = 3 \quad L = 3 \quad QI=<\text{ZIP Code,Age}> \quad SI=<\text{Salary,Disease}>
$$

Similarity attack: Low salary $\rightarrow$ Stomach disease

| **ID** | **ZIP Code** | **Age** | **Salary** | **Disease**    | **Q\*-group** |
| :----: | ------------ | :-----: | ---------- | -------------- | :-----------: |
|   1    | 476\*\*      |   2\*   | 3K         | gastric ulcer  |       1       |
|   2    | 476\*\*      |   2\*   | 4K         | gastritis      |       1       |
|   3    | 476\*\*      |   2\*   | 5K         | stomach cancer |       1       |
|   4    | 4790\*       |  >= 40  | 6K         | gastritis      |       2       |
|   5    | 4790\*       |  >= 40  | 11K        | flu            |       2       |
|   6    | 4790\*       |  >= 40  | 8K         | bronchitis     |       2       |
|   7    | 476\*\*      |   3\*   | 7K         | bronchitis     |       3       |
|   8    | 476\*\*      |   3\*   | 9K         | pneumonia      |       3       |
|   9    | 476\*\*      |   3\*   | 10K        | stomach cancer |       3       |

### Frequency analysis

Let $A_i$ be an attribute belonging to the tuple with index $i$.

We can divide the _disease_ QI into these classes

$$
\text{Pulmonary problems}=\{ 5,6,7,8 \}
$$

$$
\text{Stomach problems}=\{ 1,2,3,4,9 \}
$$

We can divide the _salary_ SI into these buckets with equal cardinality

$$
|\text{low}|=|\text{med}|=|\text{hi}|
$$

$$
\text{low} \rightarrow \{ 3K,4K,5K \}
$$

$$
\text{med} \rightarrow \{ 6K,7K,8K \}
$$

$$
\text{hi} \rightarrow \{ 9K,10K,11K \}
$$

Relative frequencies of each attribute w.r.t. the relation

$P(\text{flu})=\frac{1}{9}\approx0.111$

$P(\text{gastric ulcer})=\frac{1}{9}\approx0.111$

$P(\text{pneumonia})=\frac{1}{9}\approx0.111$

$P(\text{bronchitis})=\frac{2}{9}\approx0.222$

$P(\text{gastritis})=\frac{2}{9}\approx0.222$

$P(\text{stomach cancer})=\frac{2}{9}\approx0.222$

Relative frequencies of each attribute class

$P(\text{pulmonary problem})=\frac{4}{9}\approx0.444$

$P(\text{stomach problem})=\frac{5}{9}\approx0.555$

Relative frequencies of each attribute class w.r.t. _Salary_ buckets

$P(\text{pumonary problem}/\text{low})=\frac{0}{3}=0$

$P(\text{pumonary problem}/\text{med})=\frac{2}{3}\approx0.666$

$P(\text{pumonary problem}/\text{hi})=\frac{2}{3}\approx0.666$

$P(\text{stomach problem}/\text{low})=\frac{3}{3}=1$

$P(\text{stomach problem}/\text{med})=\frac{1}{3}\approx0.666$

$P(\text{stomach problem}/\text{hi})=\frac{1}{3}\approx0.666$

There are two (frequency based) implications that can be made

$$
P(\text{low}/\text{pumonary problem}) =
\frac{P(\text{pumonary problem}/\text{low})P(\text{low})}{P(\text{pulmunary problem})} = 0\%
$$

$\text{Knowledge}\Delta=50\%$ (assuming no background knowledge)

$$
P(\text{low}/\text{stomach problem}) =
\frac{P(\text{stomach problem}/\text{low})P(\text{low})}{P(\text{stomach problem})} = \frac{1*0.33}{0.55} = 60\%
$$

$\text{Knowledge}\Delta=10\%$ (assuming no background knowledge)
