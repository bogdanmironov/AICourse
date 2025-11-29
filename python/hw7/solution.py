import random
import math

def wcss(clusters, centroids):
    sum = 0

    for i, cluster in enumerate(clusters):
        cluster_sum = 0

        for point in cluster:
            distance_to_centroid = math.dist(point, centroids[i])
            cluster_sum += distance_to_centroid ** 2

        sum += cluster_sum

    return sum

def silhouette_score(clusters):
    total_silhouette_score = 0
    total_points = 0

    for i, cluster in enumerate(clusters):
        for point in cluster:
            if len(cluster) > 1:
                intra_distances = [math.dist(point, other_point) for other_point in cluster if point != other_point]
                a = sum(intra_distances) / len(intra_distances)

            inter_distances = []
            for j, other_cluster in enumerate(clusters):
                if i != j:
                    sum_of_distances = sum(math.dist(point, other_point) for other_point in other_cluster)
                    inter_distance = sum_of_distances / len(other_cluster)
                    inter_distances.append(inter_distance)
            b = min(inter_distances) if inter_distances else 0

            s = (b - a) / max(a, b)
            total_silhouette_score += s
            total_points += 1

    return total_silhouette_score / total_points

def kpp(data, k):
    centroids = [random.choice(data)]
    for _ in range(1, k):
        distances = [min(math.dist(point, c) for c in centroids) for point in data]
        probabilities = [dist ** 2 for dist in distances]
        probabilities = [p / sum(probabilities) for p in probabilities]
        cumulative_probs = [sum(probabilities[:i + 1]) for i in range(len(probabilities))]
        r = random.random()
        for i, cumulative_prob in enumerate(cumulative_probs):
            if r <= cumulative_prob:
                centroids.append(data[i])
                break
    return centroids

def kmeans(data, k, centroids):
    for _ in range(100):
        clusters = [[] for _ in range(k)]
        labels = []

        for point in data:
            distances = [math.dist(point, centroid) for centroid in centroids]
            cluster_i = distances.index(min(distances))
            clusters[cluster_i].append(point)
            labels.append(cluster_i)

        new_centroids = []
        for cluster in clusters:
            if cluster:
                new_centroid_coords = [sum(coord) / len(cluster) for coord in zip(*cluster)]
                new_centroids.append(new_centroid_coords)
            else:
                new_centroids.append(random.choice(data))

        if new_centroids == centroids:
            break

        centroids = new_centroids

    return clusters, centroids, labels

if __name__ == "__main__":
    file_choice = input()
    init_method = input()
    k = int(input())

    data_file = "normal.txt" if file_choice == "1" else "unbalance.txt"
    with open(data_file, 'r') as file:
        data = [tuple(map(float, line.split())) for line in file]

    centroids = random.sample(data, k) if init_method == "1" else kpp(data, k)
    clusters, centroids, labels = kmeans(data, k, centroids)

    WCSS = wcss(clusters, centroids)
    print(f"{WCSS=}")

    sc = silhouette_score(clusters)
    print(f"{sc=}")

    with open("centroids.txt", 'w') as cf:
        for centroid in centroids:
            cf.write(" ".join(map(str, centroid)) + "\n")

    with open("labels.txt", 'w') as lf:
        for label in labels:
            lf.write(f"{label}\n")
