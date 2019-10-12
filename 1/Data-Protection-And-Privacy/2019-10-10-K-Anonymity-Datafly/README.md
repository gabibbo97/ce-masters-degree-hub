# 10 October 2019

An attempt at K-Anonymity

```sh
python3 datafly.py -f dataset/db_100.csv -qi age city_birth -qi zip_code -dgh dataset/age_generalization.csv dataset/city_birth_generalization.csv -dgh dataset/zip_code_generalization.csv -k 3 -v -o test.csv
```
