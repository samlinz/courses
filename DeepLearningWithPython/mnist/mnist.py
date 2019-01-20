from keras.datasets import mnist
from keras import models
from keras import layers
from keras.utils import to_categorical
import os

CACHE_NAME = 'mnist_keras.h5'

(X_train, y_train), (X_test, y_test) = mnist.load_data()

# Load from cache if network is saved from previous run.
if os.path.exists(CACHE_NAME):
    print('Loaded from cache')
    model = models.load_model(CACHE_NAME)
else:
    print('Not cached, creating network..')

    # Create NN.
    model = models.Sequential()
    model.add(layers.Dense(10, activation='relu', input_shape=(3,)))
    model.add(layers.Dense(2, activation='softmax'))
    model.compile(optimizer='rmsprop', loss='categorical_crossentropy', metrics=['accuracy'])

    # Modify MNIST dataset to fit the input layer.
    # Namely, reshape image matrices into a 1d input tensor.
    X_train = X_train.reshape((60000, 28 * 28))
    X_train = X_train.astype('float32') / 255
    y_train = to_categorical(y_train)

    # Train the model.
    model.fit(X_train, y_train, batch_size=128, epochs=5)

    # Save to cache.
    if os.path.exists(CACHE_NAME):
        os.remove(CACHE_NAME)
    model.save(CACHE_NAME)

# Test the network using test set.
X_test = X_test.reshape((10000, 28 * 28))
X_test = X_test.astype('float32') / 255
y_test = to_categorical(y_test)

# Output network loss and accuracy.
test_loss, test_acc = model.evaluate(X_test, y_test)
print('Test loss: {}, test acc {}'.format(test_loss, test_acc))

# -- do what every you want with the trained network
