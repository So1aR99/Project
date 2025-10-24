import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
import cv2 as cv

# ① 단순 선형 회귀 (경사 하강법)

X = np.array([0.0, 1.0, 2.0])
y = np.array([3.0, 3.5, 5.5])
W = 0  # 기울기
b = 0  # 절편
lrate = 0.01  # 학습률
epochs = 1000  # 반복 횟수
n = float(len(X))  # 데이터 개수

for i in range(epochs):
    y_pred = W * X + b
    dW = (2 / n) * sum(X * (y_pred - y))
    db = (2 / n) * sum(y_pred - y)
    W = W - lrate * dW
    b = b - lrate * db

print("기울기 W:", W, "절편 b:", b)

# 그래프 출력
y_pred = W * X + b
plt.scatter(X, y, label="Data")
plt.plot(X, y_pred, color='red', label="Prediction")
plt.legend()
plt.title("Linear Regression by Gradient Descent")
plt.show()

# ② MNIST 신경망 학습
(train_images, train_labels), (test_images, test_labels) = tf.keras.datasets.mnist.load_data()
print("Train images shape:", train_images.shape)

# 데이터 전처리
train_images = train_images.reshape(60000, 28 * 28) / 255.0
test_images = test_images.reshape(10000, 28 * 28) / 255.0

# One-hot 인코딩
train_labels = tf.keras.utils.to_categorical(train_labels)
test_labels = tf.keras.utils.to_categorical(test_labels)

# 모델 구성
model = tf.keras.models.Sequential(name='MNIST')
model.add(tf.keras.layers.Input(shape=(28 * 28,)))
model.add(tf.keras.layers.Dense(512, activation='relu'))
model.add(tf.keras.layers.Dense(256, activation='relu'))
model.add(tf.keras.layers.Dense(10, activation='softmax'))

model.compile(optimizer='adam',
              loss='categorical_crossentropy',
              metrics=['accuracy'])

model.summary()

# 학습
model.fit(train_images, train_labels, epochs=10, batch_size=128, verbose=1)

# 평가
test_loss, test_accuracy = model.evaluate(test_images, test_labels)
print(f"테스트 정확도: {test_accuracy:.4f}")

# 모델 저장
model.save('MNIST_PARAM.keras')

# ③ 손글씨 이미지 불러와서 예측
# test.png 파일 읽기
image = cv.imread('test.png', cv.IMREAD_GRAYSCALE)

if image is None:
    print("⚠️ test.png 파일을 찾을 수 없습니다. 경로를 확인하세요.")
else:
    # Jupyter나 VSCode에서는 matplotlib으로 표시
    plt.imshow(image, cmap='gray')
    plt.title("Original Image (Grayscale)")
    plt.axis('off')
    plt.show()

    # 이미지 전처리
    image = cv.resize(image, (28, 28))
    image = image.astype('float32')
    image = 255.0 - image  # 색상 반전
    image /= 255.0  # 정규화
    image = image.reshape(1, 28 * 28)

    # 예측
    predict = model.predict(image, batch_size=1)
    print("예측 결과 벡터:", predict)
    print(f'가장 큰 인덱스(예측 숫자): {predict.argmax()}')

    # 예측 시각화
    plt.imshow(image.reshape(28, 28), cmap='gray')
    plt.title(f"Predicted Number: {predict.argmax()}")
    plt.axis('off')
    plt.show()
