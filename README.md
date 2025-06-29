<h1 align="center">
  URL Shortener
</h1>

<p align="center">Developed for the purpose of understanding microservices architecture, database design, architectural patterns, async workflows, system scaling, and tradeoffs while designing a scalable system.</p>

<p align="center">
  <img src="https://img.shields.io/badge/SpringBoot-20232A?style=for-the-badge&logo=springboot&logoColor=61DAFB" />
  <img src="https://img.shields.io/badge/Kafka-663399?style=for-the-badge&logo=apachekafka&logoColor=white" />
  <img src="https://img.shields.io/badge/SQL-323330?style=for-the-badge&logo=mysql&logoColor=F7DF1E" />
  <img src="https://img.shields.io/badge/Caching-1572B6?style=for-the-badge&logo=redis&logoColor=white" />
  <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white" />
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
