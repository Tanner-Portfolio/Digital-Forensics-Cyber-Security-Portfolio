# Cloud Infrastructure: Secure AWS Provisioning

## 📝 Project Overview
This project demonstrates the ability to architect and provision secure, scalable cloud infrastructure using Infrastructure as Code (IaC) principles. The lab focuses on designing an isolated Virtual Private Cloud (VPC) environment within Amazon Web Services (AWS).

## 🏗️ Technical Architecture & Tools
*   **Cloud Provider:** Amazon Web Services (AWS).
*   **Services:** AWS CloudFormation, AWS Infrastructure Composer, Amazon S3, VPC, EC2.
*   **Data Serialization:** JSON.

## 🚀 Key Features & Deployment
*   **VPC Configuration:** Provisioned a custom VPC (`10.0.0.0/16`) with DNS support and hostnames explicitly enabled.
*   **Subnet Segmentation:** Designed a secure network topology by segmenting the environment into a Public Subnet (`10.0.1.0/24`) and a Private Subnet (`10.0.2.0/24`) spanning specific Availability Zones (`us-east-2a`).
*   **Infrastructure as Code (IaC):** Utilized JSON within AWS Infrastructure Composer to dynamically generate the routing structures and resource tags.
*   **Stack Deployment:** Deployed and validated the entire architecture via an automated AWS CloudFormation Stack, ensuring repeatable and error-free infrastructure generation.

*(📸 Recommendation: Insert the AWS Infrastructure Composer Visual Map screenshot here)*
`![AWS Infrastructure Composer Map](./images/aws-composer.png)`
