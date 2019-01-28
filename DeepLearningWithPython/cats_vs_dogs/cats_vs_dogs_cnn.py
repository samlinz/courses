from keras import layers, models, activations
import matplotlib.pyplot as plt
import pickle
import sys

def plot_history(hist):
    acc = hist.history['acc']
    val_acc = hist.history['val_acc']
    loss = hist.history['loss']
    val_loss = hist.history['val_loss']

    epochs = range(1, len(acc) + 1)
    plt.plot(epochs, acc, label='Train')
    plt.plot(epochs, val_acc, label='Validation')
    plt.title('Accuracies over epochs')
    plt.legend()
    plt.show()

    plt.plot(epochs, loss, label='Train')
    plt.plot(epochs, val_loss, label='Validation')
    plt.title('Losses over epochs')
    plt.legend()
    plt.show()

# Plot training history
# with open('history.pickle', 'rb') as f:
#     hist = pickle.load(f)
# plot_history(hist)
# sys.exit(0)

# Create convnet for cat-dog image binary classification task
model = models.Sequential([
    layers.Conv2D(32, (3, 3), activation=activations.relu, input_shape=(150, 150, 3)),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation=activations.relu, input_shape=(150, 150, 3)),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(128, (3, 3), activation=activations.relu, input_shape=(150, 150, 3)),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(128, (3, 3), activation=activations.relu, input_shape=(150, 150, 3)),
    layers.MaxPooling2D((2, 2)),
    layers.Flatten(),
    layers.Dense(512, activation=activations.relu),
    layers.Dense(1, activation=activations.sigmoid)
])

from keras import optimizers, losses, metrics
from keras.preprocessing.image import ImageDataGenerator

model.compile(loss=losses.binary_crossentropy, optimizer=optimizers.RMSprop(lr=1e-4), metrics=[metrics.binary_accuracy])

# Create data generators
train_datagen = ImageDataGenerator(rescale=1./255)
test_datagen = ImageDataGenerator(rescale=1./255)
train_gen = train_datagen.flow_from_directory(
    'dataset/train', target_size=(150, 150), batch_size=20, class_mode='binary')
val_gen = train_datagen.flow_from_directory(
    'dataset/train', target_size=(150, 150), batch_size=20, class_mode='binary')

history = model.fit_generator(train_gen, steps_per_epoch=100, epochs=4, validation_data=val_gen, validation_steps=50)

# Save the history as pickle
with open('history.pickle', 'wb') as f:
    pickle.dump(history, f)
# Save the network
model.save('catsanddogs.h5')