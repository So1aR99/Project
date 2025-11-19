import time

import numpy as np
import matplotlib.pyplot as plt
import serial
import tensorflow as tf
import cv2 as cv

# 단순 선형 회귀 (경사 하강법)
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

# MNIST 신경망 학습
(train_images, train_labels), (test_images, test_labels) = tf.keras.datasets.mnist.load_data()
print("Train images shape:", train_images.shape)

# 데이터 전처리(정규화)
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

ser = serial.Serial("COM5", 115200, timeout=1)
time.sleep(2)

results = []

for i in range(1, 12):  # num1.png ~ num11.png
    filename = f"./number/num{i}.png"
    image = cv.imread(filename, cv.IMREAD_GRAYSCALE)

    if image is None:
        print(filename, "파일을 찾을 수 없습니다.")
        continue

    print(f"\n----- {i}번째 사진 -----")

    image = cv.resize(image, (28, 28))
    image = image.astype('float32')
    image = 255.0 - image   # 색상 반전
    image /= 255.0
    image = image.reshape(1, 28 * 28)

    predict = model.predict(image, batch_size=1)
    num = predict.argmax()
    print("예측된 숫자:", num)

    ser.write(str(num).encode())   # 1글자씩 누적 출력됨
    print("전송완료:", num)

    results.append(num)
    time.sleep(0.2)

    # 예측 시각화
    plt.imshow(image.reshape(28, 28), cmap='gray')
    plt.title(f"Predicted Number: {num}")
    plt.axis('off')
    plt.show()

print("\n=== 입력된 번호 ===")
result_string = "".join(str(n) for n in results)  # 리스트 → 연속 문자열 변환
print(result_string)
