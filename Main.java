// Main.java
import java.time.LocalDate;
import java.util.*;

// ==================== Задание №1. Процесс найма ====================
class Manager {
    String name;
    Manager(String name) { this.name = name; }

    JobRequest createJobRequest(String title, String requirements) {
        System.out.println(name + " создал заявку: " + title);
        return new JobRequest(title, requirements);
    }

    void reviseRequest(JobRequest req) {
        System.out.println(name + " дорабатывает заявку");
        req.setValid(true);
    }

    void conductTechInterview(Candidate c) {
        System.out.println(name + " проводит техническое интервью с " + c.name);
        c.setTechPassed(true);
    }
}

class HRDepartment {
    boolean validateRequest(JobRequest req) {
        boolean ok = req.isValid();
        System.out.println("HR проверяет заявку: " + (ok ? "OK" : "не соответствует"));
        return ok;
    }

    void rejectCandidate(Candidate c) {
        System.out.println("Кандидат " + c.name + " отклонён (анкета не подошла)");
    }

    void inviteToInterview(Candidate c) {
        System.out.println("HR приглашает на собеседование: " + c.name);
    }

    void conductHRInterview(Candidate c) {
        System.out.println("HR проводит первичное интервью с " + c.name);
        c.setHrPassed(true);
    }

    void notifyIT(Candidate hired) {
        System.out.println("HR уведомляет IT-отдел о найме " + hired.name);
    }
}

class Candidate {
    String name;
    private boolean hrPassed = false;
    private boolean techPassed = false;
    private boolean offerAccepted = false;

    Candidate(String name) { this.name = name; }

    void apply() { System.out.println(name + " подал(а) заявку"); }

    void setHrPassed(boolean val) { hrPassed = val; }
    void setTechPassed(boolean val) { techPassed = val; }

    boolean isHired() { return hrPassed && techPassed && offerAccepted; }

    void acceptOffer() {
        offerAccepted = true;
        System.out.println(name + " принял(а) оффер");
    }

    void rejectOffer() {
        System.out.println(name + " отклонил(а) оффер");
    }
}

class JobRequest {
    String title;
    String requirements;
    private boolean valid = true;

    JobRequest(String title, String requirements) {
        this.title = title;
        this.requirements = requirements;
    }

    boolean isValid() { return valid; }
    void setValid(boolean v) { valid = v; }
}

class SystemFacade {
    void publishVacancy(JobRequest req) {
        System.out.println("Система: вакансия '" + req.title + "' опубликована на сайте");
    }

    void sendOffer(Candidate c) {
        System.out.println("Система: оффер отправлен " + c.name);
    }

    void addEmployeeToDB(Candidate c) {
        System.out.println("Система: сотрудник " + c.name + " добавлен в БД компании");
    }
}

// ==================== Задание №2. Бронирование мероприятий ====================
class Venue {
    String name;
    Map<LocalDate, Boolean> availability = new HashMap<>();

    Venue(String name) { this.name = name; }

    boolean isAvailable(LocalDate date) {
        return availability.getOrDefault(date, true);
    }

    void book(LocalDate date) {
        availability.put(date, false);
    }
}

class BookingRequest {
    Venue venue;
    LocalDate date;
    BookingRequest(Venue v, LocalDate d) { venue = v; date = d; }
}

class PaymentGateway {
    boolean processPayment(double amount, String cardToken) {
        if (amount <= 0) return false;
        boolean success = Math.random() > 0.2; // 80% успеха
        System.out.println("Платёж на сумму " + amount + " через " + cardToken + ": " + (success ? "успешен" : "отклонён"));
        return success;
    }
}

class EventSystem {
    private List<Venue> venues = new ArrayList<>();
    private PaymentGateway paymentGateway = new PaymentGateway();

    public void addVenue(Venue v) { venues.add(v); }

    public String checkAvailability(LocalDate date, String venueName) {
        Optional<Venue> found = venues.stream()
                .filter(v -> v.name.equals(venueName))
                .findFirst();
        if (found.isPresent() && found.get().isAvailable(date)) {
            return "Доступно. Стоимость: 1000 у.е.";
        } else {
            return "Недоступно. Предлагаем другую дату: " + date.plusDays(7);
        }
    }

    public boolean confirmBooking(BookingRequest req, String cardToken, double amount) {
        if (!req.venue.isAvailable(req.date)) {
            System.out.println("Площадка уже занята");
            return false;
        }
        boolean paid = paymentGateway.processPayment(amount, cardToken);
        if (paid) {
            req.venue.book(req.date);
            System.out.println("Бронирование подтверждено. Администратор уведомлён.");
            return true;
        } else {
            System.out.println("Платёж отклонён. Клиенту предложено повторить оплату.");
            return false;
        }
    }
}

// ==================== Главный класс с точкой входа ====================
public class Main {
    public static void main(String[] args) {
        System.out.println("========== ЗАДАНИЕ №1: ПРОЦЕСС НАЙМА ==========");
        runHiringProcess();

        System.out.println("\n\n========== ЗАДАНИЕ №2: БРОНИРОВАНИЕ МЕРОПРИЯТИЙ ==========");
        runBookingProcess();
    }

    // Симуляция процесса найма
    private static void runHiringProcess() {
        Manager manager = new Manager("Анна (рук. отдела)");
        HRDepartment hr = new HRDepartment();
        SystemFacade system = new SystemFacade();

        // 1. Подготовительный этап
        JobRequest request = manager.createJobRequest("Java-разработчик", "Spring, микросервисы");

        if (!hr.validateRequest(request)) {
            manager.reviseRequest(request);
            if (!hr.validateRequest(request)) {
                System.out.println("Заявка отклонена окончательно. Процесс остановлен.");
                return;
            }
        }
        System.out.println("Заявка утверждена.");

        // 2. Отбор кандидатов
        system.publishVacancy(request);
        List<Candidate> candidates = List.of(new Candidate("Иван"), new Candidate("Мария"));
        List<Candidate> invited = new ArrayList<>();

        for (Candidate c : candidates) {
            c.apply();
            hr.inviteToInterview(c);
            invited.add(c);
        }

        // 3. Этап собеседования
        for (Candidate c : invited) {
            hr.conductHRInterview(c);
            manager.conductTechInterview(c);
            if (c.isHired()) {
                system.sendOffer(c);
                c.acceptOffer();
                system.addEmployeeToDB(c);
                hr.notifyIT(c);
            } else {
                System.out.println("Кандидат " + c.name + " не прошёл собеседование (отказ)");
            }
        }
    }

    // Симуляция процесса бронирования
    private static void runBookingProcess() {
        EventSystem system = new EventSystem();
        Venue hall = new Venue("Концертный зал");
        system.addVenue(hall);

        LocalDate date = LocalDate.of(2026, 6, 15);
        System.out.println("Проверка доступности: " + system.checkAvailability(date, "Концертный зал"));

        BookingRequest request = new BookingRequest(hall, date);
        boolean success = system.confirmBooking(request, "tok_1234", 1000.0);
        System.out.println("Результат бронирования: " + (success ? "успешно" : "не удалось"));
    }
}