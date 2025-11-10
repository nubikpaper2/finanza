package com.finanza.config;

import com.finanza.model.Account;
import com.finanza.model.Category;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.repository.AccountRepository;
import com.finanza.repository.CategoryRepository;
import com.finanza.repository.OrganizationRepository;
import com.finanza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Iniciando carga de datos de prueba...");
            seedData();
            log.info("Datos de prueba cargados exitosamente!");
        } else {
            log.info("Los datos ya existen, omitiendo seeder");
        }
    }

    private void seedData() {
        // Crear organización demo
        Organization demoOrg = new Organization();
        demoOrg.setName("Demo Company");
        demoOrg.setDescription("Organización de demostración");
        demoOrg.setTaxId("12345678-9");
        demoOrg.setAddress("Calle Demo 123, Ciudad Demo");
        demoOrg = organizationRepository.save(demoOrg);
        log.info("✓ Organización creada: {}", demoOrg.getName());

        // Crear usuario admin
        User admin = new User();
        admin.setEmail("admin@demo.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("Demo");
        admin.setPhone("+1234567890");
        admin.setOrganization(demoOrg);
        admin.addRole("USER");
        admin.addRole("ADMIN");
        admin = userRepository.save(admin);
        log.info("✓ Usuario Admin creado: {}", admin.getEmail());

        // Crear usuario regular
        User user = new User();
        user.setEmail("user@demo.com");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setFirstName("Usuario");
        user.setLastName("Demo");
        user.setPhone("+0987654321");
        user.setOrganization(demoOrg);
        user.addRole("USER");
        user = userRepository.save(user);
        log.info("✓ Usuario regular creado: {}", user.getEmail());

        // Crear cuentas demo
        Account cashAccount = new Account();
        cashAccount.setName("Efectivo");
        cashAccount.setType(Account.AccountType.CASH);
        cashAccount.setBalance(new BigDecimal("5000.00"));
        cashAccount.setCurrency("USD");
        cashAccount.setDescription("Caja chica y efectivo disponible");
        cashAccount.setOrganization(demoOrg);
        cashAccount.setCreatedBy(admin);
        accountRepository.save(cashAccount);
        log.info("✓ Cuenta Efectivo creada");

        Account bankAccount = new Account();
        bankAccount.setName("Banco Principal");
        bankAccount.setType(Account.AccountType.BANK);
        bankAccount.setBalance(new BigDecimal("25000.00"));
        bankAccount.setCurrency("USD");
        bankAccount.setDescription("Cuenta corriente empresarial");
        bankAccount.setOrganization(demoOrg);
        bankAccount.setCreatedBy(admin);
        accountRepository.save(bankAccount);
        log.info("✓ Cuenta Banco creada");

        Account savingsAccount = new Account();
        savingsAccount.setName("Ahorros");
        savingsAccount.setType(Account.AccountType.SAVINGS);
        savingsAccount.setBalance(new BigDecimal("10000.00"));
        savingsAccount.setCurrency("USD");
        savingsAccount.setDescription("Fondo de ahorros");
        savingsAccount.setOrganization(demoOrg);
        savingsAccount.setCreatedBy(user);
        accountRepository.save(savingsAccount);
        log.info("✓ Cuenta Ahorros creada");

        Account creditCard = new Account();
        creditCard.setName("Tarjeta de Crédito");
        creditCard.setType(Account.AccountType.CREDIT_CARD);
        creditCard.setBalance(new BigDecimal("-3500.00"));
        creditCard.setCurrency("USD");
        creditCard.setDescription("Visa Empresarial");
        creditCard.setOrganization(demoOrg);
        creditCard.setCreatedBy(admin);
        accountRepository.save(creditCard);
        log.info("✓ Cuenta Tarjeta de Crédito creada");

        Account investmentAccount = new Account();
        investmentAccount.setName("Inversiones");
        investmentAccount.setType(Account.AccountType.INVESTMENT);
        investmentAccount.setBalance(new BigDecimal("50000.00"));
        investmentAccount.setCurrency("USD");
        investmentAccount.setDescription("Cartera de inversiones");
        investmentAccount.setOrganization(demoOrg);
        investmentAccount.setCreatedBy(admin);
        accountRepository.save(investmentAccount);
        log.info("✓ Cuenta Inversiones creada");

        // Crear categorías demo
        createDemoCategories(demoOrg, admin);

        log.info("\n=== CREDENCIALES DEMO ===");
        log.info("Admin: admin@demo.com / admin123");
        log.info("User:  user@demo.com / user123");
        log.info("========================\n");
    }

    private void createDemoCategories(Organization organization, User user) {
        // Categorías de Ingresos
        createCategory("Ventas", "Ingresos por ventas de productos/servicios", 
                      Category.TransactionType.INCOME, "💰", "#10B981", organization, user);
        createCategory("Servicios", "Ingresos por prestación de servicios", 
                      Category.TransactionType.INCOME, "🛠️", "#3B82F6", organization, user);
        createCategory("Inversiones", "Rendimientos de inversiones", 
                      Category.TransactionType.INCOME, "📈", "#8B5CF6", organization, user);
        createCategory("Otros Ingresos", "Ingresos varios no categorizados", 
                      Category.TransactionType.INCOME, "💵", "#6366F1", organization, user);

        // Categorías de Gastos
        createCategory("Sueldos", "Pagos de salarios y beneficios", 
                      Category.TransactionType.EXPENSE, "👥", "#EF4444", organization, user);
        createCategory("Oficina", "Gastos de oficina y suministros", 
                      Category.TransactionType.EXPENSE, "🏢", "#F59E0B", organization, user);
        createCategory("Marketing", "Publicidad y marketing", 
                      Category.TransactionType.EXPENSE, "📢", "#EC4899", organization, user);
        createCategory("Servicios", "Servicios públicos y subscripciones", 
                      Category.TransactionType.EXPENSE, "⚡", "#F97316", organization, user);
        createCategory("Transporte", "Gastos de transporte y logística", 
                      Category.TransactionType.EXPENSE, "🚗", "#14B8A6", organization, user);
        createCategory("Alimentación", "Comidas y cafetería", 
                      Category.TransactionType.EXPENSE, "🍽️", "#84CC16", organization, user);
        createCategory("Tecnología", "Software, hardware y tecnología", 
                      Category.TransactionType.EXPENSE, "💻", "#06B6D4", organization, user);
        createCategory("Capacitación", "Cursos y entrenamiento", 
                      Category.TransactionType.EXPENSE, "📚", "#8B5CF6", organization, user);
        createCategory("Mantenimiento", "Mantenimiento y reparaciones", 
                      Category.TransactionType.EXPENSE, "🔧", "#64748B", organization, user);
        createCategory("Impuestos", "Impuestos y tasas", 
                      Category.TransactionType.EXPENSE, "🏛️", "#DC2626", organization, user);
        createCategory("Otros Gastos", "Gastos varios no categorizados", 
                      Category.TransactionType.EXPENSE, "📝", "#6B7280", organization, user);

        log.info("✓ Categorías creadas exitosamente");
    }

    private void createCategory(String name, String description, Category.TransactionType type, 
                               String icon, String color, Organization organization, User user) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setType(type);
        category.setIcon(icon);
        category.setColor(color);
        category.setOrganization(organization);
        category.setCreatedBy(user);
        categoryRepository.save(category);
    }
}
