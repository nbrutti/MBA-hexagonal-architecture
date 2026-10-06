package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.services.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateCustomerUseCaseTest {

    @Test
    @DisplayName("Deve criar um cliente")
    public void testCreateCustomer() {
        // given
        final var expectedCpf = "12345678901";
        final var expectedEmail = "john.doe@email.com";
        final var expectedName = "John Doe";
        final var createInput = new CreateCustomerUseCase.Input(expectedCpf, expectedEmail, expectedName);

        // when
        final var customerService = mock(CustomerService.class);
        when(customerService.findByCpf(expectedCpf)).thenReturn(Optional.empty());
        when(customerService.findByEmail(expectedEmail)).thenReturn(Optional.empty());
        when(customerService.save(any())).thenAnswer(a -> {
            var customer = a.getArgument(0, Customer.class);
            customer.setId(UUID.randomUUID().getMostSignificantBits());
            return customer;
        });
        final var useCase = new CreateCustomerUseCase(customerService);
        final var output = useCase.execute(createInput);

        // then
        assertNotNull(output.id());
        assertEquals(expectedCpf, output.cpf());
        assertEquals(expectedEmail, output.email());
        assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com CPF duplicado")
    public void testCreateWithDuplicatedCPFShouldFail() throws Exception {
        // given
        final var expectedCpf = "12345678901";
        final var expectedEmail = "john.doe@email.com";
        final var expectedName = "John Doe";
        final var expectedError = "Customer already exists";
        final var createInput = new CreateCustomerUseCase.Input(expectedCpf, expectedEmail, expectedName);
        final var aCustomer = new Customer();
        aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
        aCustomer.setCpf(expectedCpf);
        aCustomer.setName(expectedName);
        aCustomer.setEmail(expectedEmail);

        // when
        final var customerService = mock(CustomerService.class);
        when(customerService.findByCpf(expectedCpf)).thenReturn(Optional.of(aCustomer));
        final var useCase = new CreateCustomerUseCase(customerService);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() throws Exception {
        // given
        final var expectedCpf = "12345678901";
        final var expectedEmail = "john.doe@email.com";
        final var expectedName = "John Doe";
        final var expectedError = "Customer already exists";
        final var createInput = new CreateCustomerUseCase.Input(expectedCpf, expectedEmail, expectedName);
        final var aCustomer = new Customer();
        aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
        aCustomer.setCpf(expectedCpf);
        aCustomer.setName(expectedName);
        aCustomer.setEmail(expectedEmail);

        // when
        final var customerService = mock(CustomerService.class);
        when(customerService.findByEmail(expectedEmail)).thenReturn(Optional.of(aCustomer));
        final var useCase = new CreateCustomerUseCase(customerService);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        assertEquals(expectedError, actualException.getMessage());
    }
}
