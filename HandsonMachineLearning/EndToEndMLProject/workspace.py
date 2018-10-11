#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Oct 11 14:25:35 2018

End-To-End Machine Learning Project
Following book: Hands-On Machine Learning with Scikit-Learn and TensorFlow

@author: samlinz
"""

#%% Constants
RANDOM_SEED = 42
TEST_PORTION = 0.2
TARGET_ATTRIBUTE = 'median_house_value'

#%% Imports
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import os

#%% Load data

housing_data = pd.read_csv(os.path.join('housing', 'housing.csv'), sep=',')

#%% Explore dataset

housing_data.info()
housing_data.ocean_proximity.value_counts()

#%% Draw histograms

%matplotlib qt
housing_data.hist(bins=40, figsize=(25,15))

#%% Split to training and test sets

from sklearn.model_selection import train_test_split

train, test = train_test_split(housing_data
                               , random_state=RANDOM_SEED
                               , test_size=TEST_PORTION)

#%% Create income categories

INCOME_CATEGORY_COLUMN = 'income_cat'
housing_data[INCOME_CATEGORY_COLUMN] = np.ceil(housing_data.median_income / 1.5)
housing_data[INCOME_CATEGORY_COLUMN].where(housing_data[INCOME_CATEGORY_COLUMN] < 5
            , 5.0
            , inplace=True)

#%% Split to train and test sets representatively of income categories

from sklearn.model_selection import StratifiedShuffleSplit
strat_split = StratifiedShuffleSplit(n_splits=1
                                     , test_size=TEST_PORTION
                                     , random_state=RANDOM_SEED)
strat_gen = strat_split.split(housing_data, housing_data[INCOME_CATEGORY_COLUMN])
train, test = next(strat_gen)

#%% Visualizing geographical data

housing_data.plot(kind='scatter'
                  , x='longitude'
                  , y='latitude'
                  , alpha=0.5
                  , s=housing_data['population'] / 100 # Size represents population
                  , figsize=(25,15)
                  , c='median_house_value' # The redder, the more valuable avg house
                  , cmap=plt.get_cmap('jet'))
plt.legend()

#%% Exploring correlations

correlations = housing_data.corr()
correlations[TARGET_ATTRIBUTE].sort_values(ascending=False)

from pandas.plotting import scatter_matrix

# Plot scatter matrix
attr_to_plot = ['median_house_value'
                , 'median_income'
                , 'total_rooms'
                , 'housing_median_age']
scatter_matrix(housing_data[attr_to_plot], figsize=(25, 12))

# Median income, clearly linear correlation

#%% Create combined attributes

from sklearn.base import BaseEstimator, TransformerMixin

class HousingCombinedAttrTransformer(BaseEstimator, TransformerMixin):
    def __init__(self, ix_rooms, ix_households, ix_population, ix_bedrooms):
        self.ix_rooms = ix_rooms
        self.ix_households = ix_households
        self.ix_population = ix_population
        self.ix_bedrooms = ix_bedrooms
    
    def fit(self, X, y=None):
        return self
    
    def transform(self, X, y):
        rooms_per_household = X[:, self.ix_rooms] / X[:, self.ix_households]
        population_per_household = X[:, self.ix_population] / X[:, self.ix_households]
        bedrooms_per_household = X[:, self.ix_bedrooms] / X[:, self.ix_households]
        
        return np.c_[X, rooms_per_household
                     , population_per_household
                     , bedrooms_per_household]

