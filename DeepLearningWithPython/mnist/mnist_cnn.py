from keras import layers, optimizers, losses
from keras import models
from keras import regularizers
from keras import activations
from keras.datasets import mnist
from keras.utils import to_categorical

(X_train, y_train), (X_test, y_test) = mnist.load_data()

X_train = X_train.reshape((60_000, 28, 28, 1))
X_train = X_train.astype('float32') / 255

X_test = X_test.reshape((10_000, 28, 28, 1))
X_test = X_test.astype('float32') / 255

y_train = to_categorical(y_train)
y_test = to_categorical(y_test)

model = models.Sequential([
    layers.Conv2D(32, (3, 3), activation='relu', input_shape=(28, 28, 1)),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.Flatten(),
    layers.Dense(64, activation='relu'),
    layers.Dense(10, activation='softmax')
])

model.compile(optimizer=optimizers.RMSprop()
              , loss=losses.categorical_crossentropy
              , metrics=['accuracy'])

model.fit(X_train, y_train, epochs=5, batch_size=64)

test_loss, test_acc = model.evaluate(X_test, y_test)
print('test_loss {} test_acc {}'.format(
    test_loss,
    test_acc
))