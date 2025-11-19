import cv2
import numpy as np
import matplotlib.pyplot as plt
import keras
import serial
import time

# 1. 이미지 불러오기 (예시 1장)
image1 = cv2.imread("my_faces/img01.jpg")
cv2.imshow("FACE", image1)
cv2.waitKey(0)
cv2.destroyAllWindows()

# 2. 내 얼굴 이미지 불러오기
my_face_images = []
for i in range(15):
    file = "./my_faces/img{0:02d}.jpg".format(i + 1)
    image = cv2.imread(file)
    image = cv2.resize(image, (64, 64))
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    my_face_images.append(image)

# 3. 이미지 시각화 함수
def show_image(row, col, images):
    fig, ax = plt.subplots(row, col, figsize=(col * 2, row * 2))
    for i in range(row):
        for j in range(col):
            idx = i * col + j
            if idx < len(images):
                if row <= 1:
                    axis = ax[j]
                else:
                    axis = ax[i, j]
                axis.get_xaxis().set_visible(False)
                axis.get_yaxis().set_visible(False)
                axis.imshow(images[idx])
    plt.tight_layout()
    plt.show()

show_image(3, 5, my_face_images)

# 4. 사람얼굴 이미지 불러오기
face_images = []
for i in range(30):
    file = "./faces/img{0:02d}.jpg".format(i + 1)
    image = cv2.imread(file)
    image = cv2.resize(image, (64, 64))
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    face_images.append(image)

show_image(5, 6, face_images)

# 5. 레이블 및 데이터셋 구성
y = [(1, 0)] * len(my_face_images) + [(0, 1)] * len(face_images)
y = np.array(y)
X = my_face_images + face_images
X_train = np.array(X, dtype="float32") / 255.0

# 6. CNN 모델 정의
model = keras.Sequential(name="FACE_DETECTOR")
model.add(keras.layers.Input(shape=(64, 64, 3)))
model.add(keras.layers.Conv2D(128, (3, 3), activation='relu'))
model.add(keras.layers.MaxPooling2D(pool_size=(2, 2), strides=2))
model.add(keras.layers.Conv2D(64, (3, 3), activation='relu'))
model.add(keras.layers.MaxPooling2D(pool_size=(2, 2), strides=2))
model.add(keras.layers.Conv2D(32, (3, 3), activation='relu'))
model.add(keras.layers.MaxPooling2D(pool_size=(2, 2), strides=2))
model.add(keras.layers.Conv2D(32, (3, 3), activation='relu'))
model.add(keras.layers.MaxPooling2D(pool_size=(2, 2), strides=2))
model.add(keras.layers.Flatten())
model.add(keras.layers.Dense(64, activation='relu'))
model.add(keras.layers.Dense(64, activation='relu'))
model.add(keras.layers.Dense(32, activation='relu'))
model.add(keras.layers.Dense(2, activation='softmax'))

model.summary()
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['categorical_accuracy'])

# 7. 모델 학습 및 저장
history = model.fit(X_train, y, epochs=50)
model.save("MY_Face_DETECTOR.keras")

# 8. 모델 평가
loss, acc = model.evaluate(X_train, y, verbose=0)
print(f' 모델 학습 정확도: {acc*100:.2f}%')

# 9. 테스트 이미지 불러오기
test_images = []
for i in range(10):
    file = "./test_image/img{0:02d}.jpg".format(i + 1)
    image = cv2.imread(file)
    image = cv2.resize(image, (64, 64))
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    test_images.append(image)

show_image(2, 5, test_images)
test_images = np.array(test_images, dtype='float32') / 255.0

# 10. 예측 및 판별 출력
cnn_model = keras.models.load_model("Face_DETECTOR.keras")
predict = cnn_model.predict(test_images)

print("\n[테스트 이미지별 판별 결과]")
labels = ["MY_Face", "Other_Face"]

for i, p in enumerate(predict):
    label_index = np.argmax(p)
    confidence = p[label_index] * 100
    print(f"이미지 {i + 1:02d} → {labels[label_index]} ({confidence:.1f}%)")

# 10장 중 내 얼굴 몇 장인지 계산
my_face_count = 0
for p in predict:
    label_index = np.argmax(p)
    if label_index == 0:  # 내 얼굴로 판별
        my_face_count += 1

print(f"\n테스트 이미지 10장 중 내 얼굴은 {my_face_count}장입니다.")

ser = serial.Serial("COM5", 115200, timeout=1)
time.sleep(2)

if my_face_count >= 7:
    ser.write(b'1')
    print("\nAVR로 '1' 전송 (잠금 해제 가능)")
else:
    ser.write(b'0')
    print("\nAVR로 '0' 전송 (잠금 해제 불가)")

ser.close()

# 시각화
fig, axes = plt.subplots(2, 5, figsize=(15, 6))
axes = axes.flatten()

for i in range(len(test_images)):
    label_index = np.argmax(predict[i])
    confidence = predict[i][label_index] * 100

    # 이미지 표시
    axes[i].imshow(test_images[i])
    axes[i].axis('off')

    # 결과 텍스트 (내얼굴=파란색, 내얼굴x=빨간색)
    color = 'blue' if label_index == 0 else 'red'
    axes[i].set_title(f'{labels[label_index]}\n({confidence:.1f}%)',
                      fontsize=12, fontweight='bold', color=color)

plt.suptitle(f'My Face : {my_face_count}', fontsize=16, fontweight='bold', y=0.98)

# 범례 추가 (왼쪽 위)
fig.text(0.01, 0.94, 'Blue = MY_FACE\nRed  = OTHER',
         fontsize=11, fontweight='bold',
         bbox=dict(boxstyle='round', facecolor='white', edgecolor='gray', alpha=0.8))

plt.tight_layout()
plt.show()
