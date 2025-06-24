package io.github.danielreker.t1homeworks.service1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.danielreker.t1homeworks.service1.aop.annotation.Cached;
import io.github.danielreker.t1homeworks.service1.aop.annotation.LogDataSourceError;
import io.github.danielreker.t1homeworks.service1.aop.annotation.Metric;
import io.github.danielreker.t1homeworks.service1.mapper.ClientMapper;
import io.github.danielreker.t1homeworks.service1.model.Client;
import io.github.danielreker.t1homeworks.service1.model.dto.ClientDto;
import io.github.danielreker.t1homeworks.service1.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientMapper clientMapper;

    private final ClientRepository clientRepository;

    private final ObjectMapper objectMapper;

    @Metric
    @LogDataSourceError
    @Cached
    public Page<ClientDto> getAll(Pageable pageable) {
        Page<Client> clients = clientRepository.findAll(pageable);
        return clients.map(clientMapper::toClientDto);
    }

    @Metric
    @LogDataSourceError
    @Cached
    public ClientDto getOne(Long id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        return clientMapper.toClientDto(clientOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Metric
    @LogDataSourceError
    @Cached
    public List<ClientDto> getMany(List<Long> ids) {
        List<Client> clients = clientRepository.findAllById(ids);
        return clients.stream()
                .map(clientMapper::toClientDto)
                .toList();
    }

    @Metric
    @LogDataSourceError
    public ClientDto create(ClientDto dto) {
        Client client = clientMapper.toEntity(dto);
        Client resultClient = clientRepository.save(client);
        return clientMapper.toClientDto(resultClient);
    }

    @Metric
    @LogDataSourceError
    public ClientDto patch(Long id, JsonNode patchNode) throws IOException {
        Client client = clientRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        ClientDto clientDto = clientMapper.toClientDto(client);
        objectMapper.readerForUpdating(clientDto).readValue(patchNode);
        clientMapper.updateWithNull(clientDto, client);

        Client resultClient = clientRepository.save(client);
        return clientMapper.toClientDto(resultClient);
    }

    @Metric
    @LogDataSourceError
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Client> clients = clientRepository.findAllById(ids);

        for (Client client : clients) {
            ClientDto clientDto = clientMapper.toClientDto(client);
            objectMapper.readerForUpdating(clientDto).readValue(patchNode);
            clientMapper.updateWithNull(clientDto, client);
        }

        List<Client> resultClients = clientRepository.saveAll(clients);
        return resultClients.stream()
                .map(Client::getId)
                .toList();
    }

    @Metric
    @LogDataSourceError
    public ClientDto delete(Long id) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client != null) {
            clientRepository.delete(client);
        }
        return clientMapper.toClientDto(client);
    }

    @Metric
    @LogDataSourceError
    public void deleteMany(List<Long> ids) {
        clientRepository.deleteAllById(ids);
    }
}
