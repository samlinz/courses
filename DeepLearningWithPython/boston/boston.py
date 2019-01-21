from keras.datasets import boston_housing
from keras import layers, models, activations, optimizers, losses, metrics
from sklearn.model_selection import KFold
import numpy as np

# Load data.
(X_train, y_train), (X_test, y_test) = boston_housing.load_data()

# Normalize features.
train_mean = X_train.mean(axis=0)
train_std = X_train.std(axis=0)
X_train = (X_train - train_mean) / train_std
X_test = (X_test - train_mean) / train_std

# Number of epochs to explore.
EPOCHS = 200

# Build the model for current split.
def build_model(features):
    model = models.Sequential([
        # Input layer
        layers.Dense(64, activation=activations.relu, input_shape=(features,)),
        # Hidden layer
        layers.Dense(64, activation=activations.relu),
        # Output with no activation, linear output
        layers.Dense(1)
    ])
    model.compile(optimizer=optimizers.RMSprop()
                  , loss=losses.mean_squared_error
                  , metrics=[metrics.mae])

    return model

# List holding arrays containing each split's accuracies for each epoch.
accuracies = []

# Do a k-fold split and validate each split, append scores to global list.
k_fold = KFold(n_splits=6, shuffle=True)
k_fold = k_fold.split(X_train, y_train)
for split_index, (train_ind, test_ind) in enumerate(k_fold):
    # Get the X and y for training and testing this split.
    # Global validation set is not part of this, all is from training data.
    split_X_train = X_train[train_ind]
    split_y_train = y_train[train_ind]
    split_X_test = X_train[test_ind]
    split_y_test = y_train[test_ind]

    model = build_model(split_X_train.shape[1])

    history = model.fit(X_train
              , y_train
              , validation_data=(split_X_test, split_y_test)
              , epochs=EPOCHS
              , batch_size=1
              , verbose=0)

    # Get mean absolute error for each epoch.
    val_mae = history.history['val_mean_absolute_error']
    accuracies.append(val_mae)

# Get average accuracy for each epoch among all folds.
avg_mae_by_epoch = [np.mean([x[i] for x in accuracies]) for i in range(EPOCHS)]
print('mean accuracies by epoch {}'.format(avg_mae_by_epoch))
