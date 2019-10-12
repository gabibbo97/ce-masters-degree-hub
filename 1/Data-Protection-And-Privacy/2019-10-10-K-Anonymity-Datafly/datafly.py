#!/usr/bin/env python3

import argparse
import csv
import copy
import functools
import sys

# if __name__ == "__main__":
#   print("Please launch datafly.py instead", file=sys.stderr)
#   exit(1)

parser = argparse.ArgumentParser(description='K-Anonymity Python implementation')

parser.add_argument('-f', '--file', type=str, help='The file containing the values to anonymize')
parser.add_argument('-qi','--quasi-identifier', type=str, nargs='+', action='append', help='Column representing a quasi identifier')
parser.add_argument('-dgh','--domain-generalization-hierarchy-file', type=str, nargs='+', action='append', help='Path to domain generalization file, should be given in the same order as quasi identifiers')
parser.add_argument('-k', type=int, help='The value of k')
parser.add_argument('-o', '--output', type=str, help='Output file')
parser.add_argument('-v','--verbose', action='count', help='Verbose')

arguments = parser.parse_args(sys.argv[1:])

def flatten(array):
  return functools.reduce(lambda a,b : a + b, array)

if (arguments.domain_generalization_hierarchy_file != None):
  arguments.domain_generalization_hierarchy_file = flatten(arguments.domain_generalization_hierarchy_file)

if (arguments.quasi_identifier != None):
  arguments.quasi_identifier = flatten(arguments.quasi_identifier)


if (arguments.verbose > 0):
  print(arguments)

if (arguments.k < 1):
  print('K should be strictly positive', file=sys.stderr)
  exit(1)

class Generalization:
  edges = {}
  def __init__(self, filename):
    with open(filename) as csvfile:
      csvreader = csv.reader(csvfile, delimiter=',', quotechar='\\')
      for row in csvreader:
        # Add the edges
        for i in range(len(row) - 1):
          self.edges[row[i]] = row[i + 1]
    print(f'Created generalization with {len(self.edges)} transformations')

  def has_generalization_for_value(self, value):
    return value in self.edges

  def get_generalization_for_value(self, value):
    if not self.has_generalization_for_value(value):
      return None
    return self.edges[value]

class Generalizations:
  generalizations = {}
  def add_generalization_from_csv(self, attribute_name, filename):
    generalization = Generalization(filename)
    self.generalizations[attribute_name] = generalization
    print(f'Loaded generalization for attribute {attribute_name}')

  def get_generalization(self, attribute_name):
    if not self.has_generalization(attribute_name):
      return None
    return self.generalizations[attribute_name]

  def get_generalization_for_value(self, attribute_name, value):
    generalization = self.get_generalization(attribute_name)
    if generalization == None:
      return None
    return generalization.get_generalization_for_value(value)

  def has_generalization(self, attribute_name):
    return attribute_name in self.generalizations

available_generalizations = Generalizations()

for i in range(len(arguments.quasi_identifier)):
  quasi_identifier_name = arguments.quasi_identifier[i]
  domain_generalization_hierarchy_file_name = arguments.domain_generalization_hierarchy_file[i]
  available_generalizations.add_generalization_from_csv(quasi_identifier_name, domain_generalization_hierarchy_file_name)

class PrivateData:
  data = []
  quasi_identifier_frequencies = {}
  def __init__(self, filename, quasi_identifiers):
    with open(filename) as csvfile:
      csvreader = csv.reader(csvfile, delimiter=',', quotechar='\\')
      for row in csvreader:
        if not hasattr(self, 'attribute_names'):
          self.attribute_names = row
        else:
          self.data.append(row)
    print(f'Loaded private data for {len(self.data)} records')
    self.quasi_identifiers = quasi_identifiers

  def anonymize(self, k):
    print(f'Starting anonimization for k={k}')
    # Build frequencies
    for i in range(len(self.data)):
      elem = self.data[i]
      quasi_identifier_tuple = self.get_quasi_identifier_tuple_for_row(elem)
      if quasi_identifier_tuple in self.quasi_identifier_frequencies:
        self.quasi_identifier_frequencies[quasi_identifier_tuple].add(i)
      else:
        self.quasi_identifier_frequencies[quasi_identifier_tuple] = set([i])
    print('Built initial frequency data')
    print(self.quasi_identifier_frequencies)
    # Main loop
    anonymization_pass = 1
    while self.anonimization_loop_condition(k):
      best_attribute_to_anonymize = self.find_best_attribute_for_anonymization()
      print(f'Anonymization pass {anonymization_pass} has selected attribute {best_attribute_to_anonymize}')
      self.generalize(best_attribute_to_anonymize)
      anonymization_pass += 1
    print(f'Anonymization ended after {anonymization_pass} generalization passes')
    self.suppress(k)
    print(f'Printing CSV')
    with open(file=arguments.output, mode='w') as csvfile:
      csvwriter = csv.writer(csvfile)
      csvwriter.writerow(self.attribute_names)
      for row_ids_set in self.quasi_identifier_frequencies.values():
        for row_id in row_ids_set:
          csvwriter.writerow(self.data[row_id])
    # Print stats
    print(f'Frequency data: ({", ".join(self.quasi_identifiers)})')
    for quasi_identifier_tuple, quasi_identifier_ids_set in self.quasi_identifier_frequencies.items():
      print(f'{quasi_identifier_tuple} -> {len(quasi_identifier_ids_set)}')
    print('Wrote output')

  def anonimization_loop_condition(self, k):
    non_ok_count = 0
    for quasi_identifier_ids_set in self.quasi_identifier_frequencies.values():
      if len(quasi_identifier_ids_set) < k:
        non_ok_count += 1
    print(f'Found {non_ok_count} non ok tuples')
    return non_ok_count > k

  def find_best_attributes_for_anonymization(self):
    candidates = {}
    for i in range(len(self.quasi_identifiers)):
      candidate_name = self.quasi_identifiers[i]
      distinct_values = set([x[i] for x in self.quasi_identifier_frequencies.keys()])
      candidates[candidate_name] = len(distinct_values)
    for key, value in candidates.items():
      print(f'{key} has {value} distinct values')
    return sorted(candidates, reverse=True, key=lambda attribute_name: candidates[attribute_name])

  def find_best_attribute_for_anonymization(self):
    return self.find_best_attributes_for_anonymization()[0]

  def generalize(self, attribute_name):
    print(f'Starting generalization for attribute {attribute_name}')
    quasi_identifier_index = self.quasi_identifiers.index(attribute_name)
    row_quasi_identifier_index = self.attribute_names.index(attribute_name)
    # copy() is needed because we are performing modifications while iterating
    freq_copy = copy.deepcopy(self.quasi_identifier_frequencies)
    for quasi_identifier_tuple, quasi_identifier_ids_set in freq_copy.items():
      # Skip if generalization is not avilable
      if not available_generalizations.has_generalization(attribute_name):
        print(f'Generalization not available for {attribute_name}')
        continue
      # Skip if a generalized value was not found
      generalized_value = available_generalizations.get_generalization_for_value(attribute_name, quasi_identifier_tuple[quasi_identifier_index])
      if generalized_value == None:
        print(f'Generalization not found for {attribute_name} = {quasi_identifier_tuple[quasi_identifier_index]}')
        continue
      # Generalize the rows
      for row_id in quasi_identifier_ids_set:
        # Generalize the value
        self.data[row_id][row_quasi_identifier_index] = generalized_value
      # Update the frequencies
      first_row_id = next(iter(quasi_identifier_ids_set))
      new_quasi_identifier_tuple = self.get_quasi_identifier_tuple_for_row(self.data[first_row_id])
      # Create the set for new identifier
      if new_quasi_identifier_tuple not in self.quasi_identifier_frequencies:
        self.quasi_identifier_frequencies[new_quasi_identifier_tuple] = set()
      # Edit the sets for old identifiers
      for row_id in quasi_identifier_ids_set:
        # Remove from old set
        self.quasi_identifier_frequencies[quasi_identifier_tuple].remove(row_id)
        # Add to new set
        self.quasi_identifier_frequencies[new_quasi_identifier_tuple].add(row_id)
      # Cleanup old frequencies
      if len(self.quasi_identifier_frequencies[quasi_identifier_tuple]) == 0:
        del self.quasi_identifier_frequencies[quasi_identifier_tuple]

  def suppress(self, k):
    suppression_count = 0
    # copy() is needed because we are performing modifications while iterating
    freq_copy = copy.deepcopy(self.quasi_identifier_frequencies)
    for quasi_identifier_tuple, quasi_identifier_ids_set in freq_copy.items():
      if len(quasi_identifier_ids_set) >= k:
        continue
      # Suppress the tuples
      suppression_count += len(quasi_identifier_ids_set)
      # Remove its frequency data
      del self.quasi_identifier_frequencies[quasi_identifier_tuple]
    print(f'Suppressed {suppression_count} tuples')


  def get_quasi_identifier_tuple_for_row(self, row):
    quasi_identifier_columns = []
    for i in range(len(row)):
      if self.attribute_names[i] in self.quasi_identifiers:
        quasi_identifier_columns.append(row[i])
    return tuple(quasi_identifier_columns)

private_data = PrivateData(arguments.file, arguments.quasi_identifier)
private_data.anonymize(arguments.k)
