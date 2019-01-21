from keras import models, layers, activations, optimizers, losses, metrics
from keras.datasets import imdb
import matplotlib.pyplot as plt
import numpy as np

(X_train, y_train), (X_test, y_test) = imdb.load_data(num_words=10_000)

# Get dictionary which maps integer indices to words to explore the dataset.
word_index = imdb.get_word_index()
words_by_index = dict([(int_value, word) for (word, int_value) in word_index.items()])


def print_review_text(review_values):
    text = ' '.join([words_by_index.get(i - 3, '?') for i in review_values])
    print(text)

def onehot_reviews(sequences, words):
    result = np.zeros((len(sequences), words))
    for i, words in enumerate(sequences):
       result[i, words] = 1
    return result

def plot_loss_acc(train_history, epochs, show_fig=True, save_fig=False):
    x_axis = range(1, epochs + 1)
    history = train_history.history

    # Plot losses.
    train_loss = history['loss']
    train_val_loss = history['val_loss']
    plt.scatter(x_axis, train_loss, label='Training loss')
    plt.scatter(x_axis, train_val_loss, label='Validation loss')
    plt.legend()

    fig = plt.gcf()

    if show_fig:
        print('Showing losses')
        fig.show()
    if save_fig:
        print('Saving losses')
        fig.savefig('losses.png')

    # Clear
    plt.clf()

    # Plot accuracies.
    train_acc = history['binary_accuracy']
    train_val_acc = history['val_binary_accuracy']
    plt.scatter(x_axis, train_acc, label='Training accuracy')
    plt.scatter(x_axis, train_val_acc, label='Validation accuracy')
    plt.legend()

    fig = plt.gcf()

    if show_fig:
        print('Showing accuracy')
        fig.show()
    if save_fig:
        print('Saving accuracy')
        fig.savefig('accuracy.png')


# Create "one-hot encoded" matrix of the dataset, in which one column matches one word.
X_train = onehot_reviews(X_train, 10_000)
X_test = onehot_reviews(X_test, 10_000)

y_train = np.asarray(y_train).astype(np.float32)
y_test = np.asarray(y_test).astype(np.float32)

# Create model.
model = models.Sequential()
model.add(layers.Dense(16, activation=activations.relu, input_shape=(10_000,)))
model.add(layers.Dense(16, activation=activations.relu))
model.add(layers.Dense(1, activation=activations.sigmoid))
model.compile(optimizer=optimizers.RMSprop(lr=0.001), loss=losses.binary_crossentropy, metrics=[metrics.binary_accuracy])

EPOCHS = 20

# Train network.
train_history = model.fit(X_train, y_train, epochs=EPOCHS, batch_size=512, validation_split=0.1)

# Plot the loss and binary accuracy over epochs to evaluate how many epochs should the actual model train.
plot_loss_acc(train_history, EPOCHS, True, True)
