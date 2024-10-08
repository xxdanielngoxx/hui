package com.github.xxdanielngoxx.hui.api.owner.controller;

import com.github.xxdanielngoxx.hui.api.owner.controller.mapper.RegisterOwnerMapper;
import com.github.xxdanielngoxx.hui.api.owner.controller.request.RegisterOwnerRequest;
import com.github.xxdanielngoxx.hui.api.owner.service.RegisteringOwnerService;
import com.github.xxdanielngoxx.hui.api.owner.service.command.RegisterOwnerCommand;
import com.github.xxdanielngoxx.hui.api.shared.error.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(path = "/api/v1/owners")
@Tag(name = "owners")
@RequiredArgsConstructor
public class OwnerController {

  private final RegisteringOwnerService registeringOwnerService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Register an owner",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Registering owner successful",
            headers = {
              @Header(
                  name = "Location",
                  schema = @Schema(implementation = String.class),
                  description = "URI to get registered owner")
            }),
        @ApiResponse(
            responseCode = "400",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  public ResponseEntity<Void> register(@Valid @RequestBody final RegisterOwnerRequest request) {

    final RegisterOwnerCommand command = RegisterOwnerMapper.INSTANCE.requestToCommand(request);
    final UUID createdOwnerId = registeringOwnerService.register(command);

    final URI createdOwnerLocation =
        UriComponentsBuilder.fromPath("/api/v1/owners/{id}").buildAndExpand(createdOwnerId).toUri();

    return ResponseEntity.created(createdOwnerLocation).build();
  }
}
