# FoodRescue

A Spring Boot web app that connects restaurants/shops with surplus food to NGOs and
volunteers who can pick it up and distribute it before it goes to waste. Built as our
semester project for the Software Engineering course at Southeast University, Dept. of CSE.

## The problem we're trying to solve

A lot of usable food gets thrown away by restaurants and event caterers in Dhaka every
day, while NGOs that run food distribution for low-income families often don't know
where or when surplus food is available. FoodRescue is a middle layer: providers post
what they have, NGOs/volunteers request it, and the provider picks whoever is best
suited (based on rating, distance, and how they plan to distribute it).

## Team

| Name            | Part |
|-----------------|---|
| Javer Hossain   | Database design, project setup, repo owner |
| Samsul Alam         | Entity classes (JPA models) and Repositories |
| Mahfuzul Alam Mehedi          | Service layer / business logic (urgency ranking, claim workflow, ratings) |
| Kohinur Khatun  | Controllers and request routing |
| Jannatul Naieem | Frontend (Thymeleaf templates + Tailwind) |

## Tech stack

- Java 17, Spring Boot 3
- Spring MVC + Thymeleaf for the frontend (Tailwind CSS via CDN)
- Spring Data JPA + PostgreSQL
- BCrypt for password hashing
- Maven

## Core features

- Three account types: **Food Provider**, **NGO**, **Volunteer**
- NGOs and Volunteers have to upload an NID/document when they sign up, and an admin
  manually verifies it before it shows as "Verified" (there's no auto-verification —
  that would need a government ID API we don't have access to)
- Providers post surplus food with quantity, pickup location, deadline, a freshness
  tag, and roughly how many people it can feed
- Food automatically expires in the system once the pickup deadline passes
- NGOs/Volunteers can request food and have to explain how/where they'll distribute it
- If more than one group requests the same food, the provider picks one — seeing
  each requester's rating, distance, and verification status before deciding
- After a pickup, the provider rates the NGO/Volunteer (1–5 stars), and the
  claimant uploads proof of distribution (photo + short report)
- A simple leaderboard ranks NGOs/Volunteers by completed pickups, and providers
  get a "Recurring Donor" badge after 5 completed donations
- NGOs/Volunteers can post upcoming distribution events; providers browsing nearby
  can see them and plan a donation around it
- Basic in-app notifications when new food or an event is posted in your city
  (this is NOT real push/SMS — we didn't integrate Firebase/Twilio, it's just an
  in-app alert, noted as a limitation below)
- A flag/report button on listings in case something looks like a scam or fake post
- A "food journey" timeline per request: posted → requested → accepted → picked up → distributed

## Project structure

```
src/main/java/com/foodrescue/
  model/        - JPA entities (User, FoodListing, FoodClaim, Rating, Event, ...)
  repository/   - Spring Data JPA repositories
  service/      - business logic (AuthService, FoodListingService, NotificationService, ...)
  controller/   - MVC controllers
  dto/          - form objects used by the controllers
  security/     - SessionUser (simple session-based login, not full Spring Security)
  config/       - static file serving for uploaded documents/photos
src/main/resources/
  schema.sql    - full DB schema, runs automatically on startup
  templates/    - Thymeleaf + Tailwind pages
```

## Running it locally

1. Create a PostgreSQL database called `foodrescue`
2. Set your DB password in `src/main/resources/application.properties`
3. Run `FoodRescueApplication.java` — the schema is created automatically from `schema.sql`
4. Go to `http://localhost:8080`

To make an account an admin (so it can review NID verifications and reports), run
this manually in pgAdmin — there is no sign-up path for admin accounts, on purpose:

```sql
UPDATE users SET is_admin = TRUE WHERE email = 'your-email@example.com';
```

## Known limitations / what we'd do next

- Distance between provider and claimant only works if both sides entered latitude/longitude
  manually — we didn't hook up a geocoding API, so it's optional, not automatic
- "Nearby" notifications are matched by city name, not actual GPS radius
- No real-time chat between provider and claimant yet — phone number is shared after
  a request is accepted
- Admin verification is single-admin, no audit log of who approved what
- Session-based login only, not full Spring Security (no OAuth, no 2FA)

## Database

9+ tables covering users, the three profile types (providers/organizations/volunteers),
food listings, claims, ratings, events, notifications, and reports. Full schema with
comments is in `src/main/resources/schema.sql`.
