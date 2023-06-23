# CRMj
Contact Relationship Management System - the `j` is for either Java or Jannetta (or both)

I have been browsing to find a CRM that suits my needs. I found that I could either pay (commercial software) and not quite have what I need, or not pay (open source) and also not quite have what I need. Here is what I want and this application will hopefully, eventually, develop into that.

## Requirements
### Non functional requirements
1. [ ] To be written in **Java**
1. [ ] We'll be using **IntelliJ** so the directory structure will reflect that.
1. [ ] We'll be using **Maven**
1. [ ] To use **SparkJava** with **Jetty**
  - [ ] [https://sparkjava.com/tutorials/jetty-request-log](https://sparkjava.com/tutorials/jetty-request-log)
  - [ ] [https://sparkjava.com/tutorials/application-structure](https://sparkjava.com/tutorials/application-structure)
1. [ ] To use **SQLite** for a database
1. [ ] A REST service
1. [ ] A Browser user interface
1. [ ] To run, like [OpenRefine](https://openrefine.org/) as a web service but on your local machine so that you do not require Internet activity and you do not have to consider all the security implications of running on public servers.
   
### Considerations for later
1. To add an alternative Swing (or something) desktop user interface
2. To add security via login for multi-user
3. To make provision for other databases. The design will use DAO to provide for this from the beginning.

### Functional Requirements
#### Contacts
- [ ] Capture contact information with a UUID as the primary key
- [ ] A contact will have:
  - [ ] Zero or more telephone numbers
  - [ ] Zero or more email addresses
  - [ ] Zero or more social media types (the type and its link) to be picked from a list (enumeration) eg. LinkedIn, Slack, WhatsApp, Facebook, Instagram, Telegram, Signal. 
- [ ] Contacts are of enumeration type contact_category. A contact can fall into more than one category:
  - [ ] personal
  - [ ] colleague
  - [ ] ...
     
#### Interactions
- [ ] The user will have interactions with contacts. These interactions are of enumeration type interaction_type:
  - [ ] phone
  - [ ] email
  - [ ] video (we might want to split this into video type eg zoom, teams, skype - what will be implications on design?)
  - [ ] in-person
- [ ] Interaction will also record other people that were involved in the interaction - people are selected from the contact list.
- [ ] (I might regret this later) The application is for a single user and login should only require a password.
- [ ] There should be a dashboard with tabs/links/options/buttons to add:
  - [ ] Contacts
  - [ ] Interactions - if any of the items such as contacts or interaction types do not exist, one should be able to add them from this screen, i.e. without having to reverse to the dashboard and enter new contacts, interactions types etc. from there.
  - [ ] Contact types
  - [ ] Interaction types
  - [ ] Social media types

#### Conventions

- [Hungarian notation](https://en.wikipedia.org/wiki/Hungarian_notation#Examples)
- Private member
  - Default - m_camelCase
  - Static - s_camelCase
  - Constant - m_UPPER_CASE
  - Static constant - s_UPPER_CASE
- Public member
  - Default - camelCase
  - Constant - UPPER_CASE
- Avoid public members where possible (prefer getters/setters)
- Prefer Path over File and File over String
- Single instance classes (like CRMjServer/CRMjProperties) should always be final (to avoid accidental overwrites of references)

