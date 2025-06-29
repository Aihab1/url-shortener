<h1 align="center">
  URL Shortener
</h1>

<p align="center">Developed for the purpose of understanding microservices architecture, database design, architectural patterns, async workflows, system scaling, and tradeoffs while designing a scalable system.</p>

<p align="center">
  <img src="https://img.shields.io/badge/SpringBoot-20232A?style=for-the-badge&logo=springboot&logoColor=61DAFB" />
  <img src="https://img.shields.io/badge/Kafka-663399?style=for-the-badge&logo=apachekafka&logoColor=white" />
  <img src="https://img.shields.io/badge/SQL-323330?style=for-the-badge&logo=mysql&logoColor=F7DF1E" />
  <img src="https://img.shields.io/badge/Caching-1572B6?style=for-the-badge&logo=redis&logoColor=white" />
  <img src="https://img.shields.io/badge/Excalidraw-F24E1E?style=for-the-badge&logo=figma&logoColor=white" />
</p>

## How does a URL shortener work?
- the server generates a unique short URL for each long URL
- the server encodes the short URL for readability
- the server persists the short URL in the data store
- the server redirects the client to the original long URL against the short URL

## Useful terminologies used in design
- **Microservices**: designing software that is made up of small independent services, which have a specific purpose
- **Service Discovery**: the process of automatically detecting devices and services on a network
- **CDN**: a group of geographically distributed servers that speed up the delivery of web content by bringing the content closer to the users
- **API**: a software intermediary that allows two applications or services to talk to each other
- **Encoding**: the process of converting data from one form to another to preserve the usability of data
- **Encryption**: secure encoding of data using a key to protect the confidentiality of data
- **Hashing**: a one-way summary of data that cannot be reversed and is used to validate the integrity of data
- **Bloom filter**: a memory-efficient probabilistic data structure to check whether an element is present in a set

## Database design
![db-design](https://github.com/user-attachments/assets/ed88bbb7-2bde-4956-b8cc-f1401cd4aeb0)

## Initial project architecture
Keeping it basic for project's purpose - this can be scaled by inserting CDNs, Redis for caching, bloom filters for checking duplicate hashes, etc.
![high-level-design](https://github.com/user-attachments/assets/52201be4-8521-4ee9-b460-0c6689827fee)

## High level design
### Encoding
Encoding is the process of converting data from one form to another. The following are the reasons to encode a shortened URL:
- improve the readability of the short URL
- make short URLs less error-prone
- The encoding format to be used in the URL shortener must yield a deterministic (no randomness) output.

The characters in base62 encoding consume 6 bits (2⁶ = 64). A short URL of 7 characters in length in base62 encoding consumes 42 (7*6) bits.
The following generic formula is used to count the total number of short URLs that are produced using a specific encoding format and the number of characters in the output:
**Total count of short URLs = encoding format ^ length**: 62 ^ 7 = 3.5 trillion URLs

The total count of short URLs is directly proportional to the length of the encoded output. However, the length of the short URL must be kept as short as possible for better readability. The base62 encoded output of 7-character length generates 3.5 trillion short URLs. A total count of 3.5 trillion short URLs is exhausted in 100 years when 1000 short URLs are used per second. The guidelines on the encoded output format to improve the readability of a short URL are the following:
- the encoded output contains only alphanumeric characters
- the length of the short URL must not exceed 9 characters

The time complexity of base conversion is O(k), where k is the number of characters (k = 7). The time complexity of base conversion is reduced to constant time O(1) because the number of characters is fixed.

*In summary, a 7-character base62 encoded output satisfies the system requirement.*

### Write path (generating a short url)
The server shortens the long URL entered by the client. The shortened URL is encoded for improved readability. The server persists the encoded short URL into the database. The simplified block diagram of a single-machine URL shortener is the following:
<img src="https://systemdesign.one/url-shortening-system-design/simplified-url-shortener.webp" />

The single-machine solution does not meet the scalability requirements of the URL shortener system. The key generation function is moved out of the server to a dedicated Key Generation Service (KGS) to scale out the system.
<img src="https://systemdesign.one/url-shortening-system-design/url-shortener-key-generation-service.webp" />

The different solutions to shortening a URL are the following:
- Random ID Generator ❌
- Hashing Function ❌
- Token Range ✅

#### Random ID Generator
The Key Generation Service (KGS) queries the random identifier (ID) generation service to shorten a URL. The service generates random IDs using a random function or Universally Unique Identifiers (UUID). Multiple instances of the random ID generation service must be provisioned to meet the demand for scalability.
<img src="https://systemdesign.one/url-shortening-system-design/url-shortener-random-id-generator.webp" />

*The random ID generation solution has the following tradeoffs:*
- *the probability of collisions is high due to randomness*
- *breaks the 1-to-1 mapping between a short URL and a long URL*
- *coordination between servers is required to prevent a collision*
- *frequent verification of the existence of a short URL in the database is a bottleneck*

#### Hashing Function
The KGS queries the hashing function service to shorten a URL. The hashing function service accepts a long URL as an input and executes a hash function such as the message-digest algorithm (MD5) to generate a short URL. The length of the MD5 hash function output is 128 bits.

<img src="https://systemdesign.one/url-shortening-system-design/url-shortener-hashing-long-url.webp" />

The hashing function service is replicated to meet the scalability demand of the system. Instances of this service should not be load-balanced by hashing algorithms (for eg. consistent hashing) because it'll overload one server if the same long URL is entered by a large number of clients at the same time.
Use **round-robin, least connection, least bandwidth**
<img src="https://systemdesign.one/url-shortening-system-design/url-shortener-hashing-function-service.webp" />

*The base62 encoding of MD5 output yields 22 characters because each base62 encoded character consumes 6 bits and MD5 output is 128 bits. The encoded output must be truncated by considering only the first 7 characters (42 bits) to keep the short URL readable. However, the encoded output of multiple long URLs might yield the same prefix (first 7 characters), resulting in a collision. Random bits are appended to the suffix of the encoded output to make it nonpredictable at the expense of short URL readability.*

#### Token Range
The KGS queries the token service to shorten a URL. An internal counter function of the token service generates the short URL and the output is monotonically increasing.
<img src="https://systemdesign.one/url-shortening-system-design/url-shortener-token-service-consistent-hash-ring.webp" />

The output of the token service instances (distributed system) must be non-overlapping to prevent a collision. A highly reliable distributed service such as Apache Zookeeper or Amazon DynamoDB is used to coordinate the output range of token service instances. The service that coordinates the output range between token service instances is named the token range service. Stronger consistency prevents a range collision by preventing fetching the same output range by multiple token services.
<img src="https://systemdesign.one/url-shortening-system-design/url-shortener-token-range-service.webp" />

The token range service might become a bottleneck if queried frequently. Either the output range or the number of token range service replicas must be incremented to improve the reliability of the system. The token range solution is collision-free and scalable.

The time complexity of short URL generation using token service is constant O(1). In contrast, the KGS must perform one of the following operations before shortening a URL to preserve the 1-to-1 mapping:
- query the database to check the existence of the long URL
- use the putIfAbsent procedure to check the existence of the long URL
Querying the database is an expensive operation because of the disk input/output (I/O) and most of the NoSQL data stores do not support the putIfAbsent procedure due to eventual consistency.
<img src="https://systemdesign.one/url-shortening-system-design/url-shortener-bloom-filter.webp" />

**A bloom filter is used to prevent expensive data store lookups on URL shortening. The time complexity of a bloom filter query is constant O(1). The KGS populates the bloom filter with the long URL after shortening the long URL. When the client enters a customized URL, the KGS queries the bloom filter to check if the long URL exists before persisting the custom short URL into the data store.**

When the client enters an already existing long URL, the KGS must return the appropriate short URL **but** the database is partitioned with the short URL as the partition key. The short URL as the partition key resonates with the read and write paths of the URL shortener.

A naive solution to finding the short URL is to build an index on the long URL column of the data store. However, the introduction of a database index degrades the write performance and querying remains complex due to sharding using the short URL key.

The optimal solution is to introduce an additional data store (inverted index) with mapping from the long URLs to the short URLs (key-value schema). The additional data store improves the time complexity of finding the short URL of an already existing long URL record. On the other hand, an additional data store increases storage costs. The additional data store is partitioned using consistent hashing. The partition key is the long URL to quickly find the URL record. A key-value store such as DynamoDB is used as the additional data store.
<img src="https://systemdesign.one/url-shortening-system-design/url-shortener-inverted-index-store.webp" />

### Read path (getting a shortened url)
The server redirects the shortened URL to the original long URL. The simplified block diagram of a single-machine URL redirection is the following:
<img src="https://systemdesign.one/url-shortening-system-design/simplified-url-shortener.webp" />

The single-machine solution does not meet the scalability requirements of URL redirection for a read-heavy system. The disk I/O due to frequent database access is a potential bottleneck.

The URL redirection traffic (Egress) is cached following the 80/20 rule to improve latency. The cache stores the mapping between the short URLs and the long URLs. The cache handles uneven traffic and traffic spikes in URL redirection. The server must query the cache before hitting the data store. The cache-aside pattern is used to update the cache. When a cache miss occurs, the server queries the data store and populates the cache. The tradeoff of using the cache-aside pattern is the delay in initial requests. As the data stored in the cache is memory bound, the Least Recently Used (LRU) policy is used to evict the cache when the cache server is full.

<img src="https://systemdesign.one/url-shortening-system-design/url-redirection-caching-different-layers.webp" />
The cache and the data store must not be queried if the short URL does not exist. A bloom filter on the short URL is introduced to prevent unnecessary queries. If the short URL is absent in the bloom filter, return an HTTP status code of 404. If the short URL is set in the bloom filter, delegate the redirection request to the cache server or the data store.

The cache servers are scaled out by performing the following operations:
- partition the cache servers (use the short URL as the partition key)
- replicate the cache servers to handle heavy loads using leader-follower topology
- redirect the write operations to the leader
- redirect all the read operations to the follower replicas
<img src="https://systemdesign.one/url-shortening-system-design/url-redirection-scaling-cache-servers.webp" />

This design has been heavily taken and understood from <a href="https://systemdesign.one/url-shortening-system-design/" target="_blank">systemdesign.one</a>. Please visit the source for a more deep dive into design, its functional and non-functional components.
