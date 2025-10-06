# ReferralSystem
ReferralSystem - a scalable, modular framework for managing job referrals efficiently. At its core, ReferralSystem uses interface-based design and object serialization to provide a clean, extensible structure that can adapt to multiple companies.
Highlights:
Polymorphism in Action: Uses the Company interface to enable seamless integration for multiple organizations without changing the core system.
Persistence: Stores referral data persistently using Java serialization (.ser files), ensuring data survives between runs.
Extensible Commands: Supports CLI commands for committing resumes, admitting and evicting referrals, editing statuses, and viewing data.
Encapsulation & Modularity: Core components like Monitor, Value, and Commit keep business logic clean and maintainable.

Why It Matters?
In modern hiring workflows, managing referrals is critical yet complex. This system provides a centralized, automated, and scalable approach that simplifies the process, improves tracking, and opens the door for AI integration and advanced analytics in the future.
