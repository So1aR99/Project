#  KNN을 이용한 닥스훈트 vs 사모예드 분류

import numpy as np
import matplotlib.pyplot as plt
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

# 원본 데이터
dachshund_length = [77, 78, 85, 83, 73, 77, 73, 80]
dachshund_height = [25, 28, 29, 30, 21, 22, 17, 35]

samoyed_length = [75, 77, 86, 86, 79, 83, 83, 88]
samoyed_height = [56, 57, 50, 53, 60, 53, 49, 61]

# 시각화
plt.scatter(dachshund_length, dachshund_height, c='r', marker='.', label='Dachshund')
plt.scatter(samoyed_length, samoyed_height, c='b', marker='*', label='Samoyed')
plt.legend()
plt.show()

# 미지의 강아지 데이터
unknown_dog = [[79, 35]]
plt.scatter(dachshund_length, dachshund_height, c='r', marker='.')
plt.scatter(samoyed_length, samoyed_height, c='b', marker='*')
plt.scatter(79, 35, c='c', marker='p', label='Unknown')
plt.legend()
plt.show()

# 라벨링 및 데이터 결합
dachshund_label = np.zeros(len(dachshund_length))
samoyed_label = np.ones(len(samoyed_length))

dachshund_data = np.column_stack((dachshund_length, dachshund_height))
samoyed_data = np.column_stack((samoyed_length, samoyed_height))

dogs = np.concatenate((dachshund_data, samoyed_data))
labels = np.concatenate((dachshund_label, samoyed_label))

# KNN 모델 학습
knn = KNeighborsClassifier(n_neighbors=3)
knn.fit(dogs, labels)
print("Unknown dog 예측:", knn.predict(unknown_dog))

# 데이터 증강 (통계적 기반)
dachshund_length_mean = np.mean(dachshund_length)
dachshund_height_mean = np.mean(dachshund_height)
samoyed_length_mean = np.mean(samoyed_length)
samoyed_height_mean = np.mean(samoyed_height)

new_dachshund_length = np.random.normal(dachshund_length_mean, 7, 200)
new_dachshund_height = np.random.normal(dachshund_height_mean, 7, 200)
new_samoyed_length = np.random.normal(samoyed_length_mean, 7, 200)
new_samoyed_height = np.random.normal(samoyed_height_mean, 7, 200)

# 증강 데이터 시각화
plt.scatter(new_dachshund_length, new_dachshund_height, c='r', marker='.', label='Dachshund')
plt.scatter(new_samoyed_length, new_samoyed_height, c='b', marker='*', label='Samoyed')
plt.xlabel('Length')
plt.ylabel('Height')
plt.title('Dog size')
plt.legend(loc='upper left')
plt.show()

# 증강 데이터 병합 및 라벨 생성
new_dachshund_data = np.column_stack((new_dachshund_length, new_dachshund_height))
new_samoyed_data = np.column_stack((new_samoyed_length, new_samoyed_height))

new_dachshund_label = np.zeros(len(new_dachshund_data))
new_samoyed_label = np.ones(len(new_samoyed_data))

new_dogs = np.concatenate((new_dachshund_data, new_samoyed_data))
new_labels = np.concatenate((new_dachshund_label, new_samoyed_label))

# 학습 / 테스트 데이터 분리
X_train, X_test, y_train, y_test = train_test_split(new_dogs, new_labels, test_size=0.2, random_state=0)

# KNN 모델 학습 및 평가
k = 5
knn = KNeighborsClassifier(n_neighbors=k)
knn.fit(X_train, y_train)

print(f"훈련 정확도: {knn.score(X_train, y_train):.3f}")

y_predict = knn.predict(X_test)
print("테스트 예측:", y_predict)
print("테스트 정답:", y_test)
print(f"테스트 정확도: {accuracy_score(y_test, y_predict):.3f}")
