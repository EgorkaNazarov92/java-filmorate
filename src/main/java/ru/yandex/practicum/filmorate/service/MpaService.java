package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Service
public class MpaService {
	private final MpaRepository mpaRepository;

	public MpaService(MpaRepository mpaRepository) {
		this.mpaRepository = mpaRepository;
	}

	public Mpa getMpa(Integer id) {
		Optional<Mpa> mpa = mpaRepository.getMpa(id);
		if (mpa.isEmpty()) throw new NotFoundException("MPA с id = " + id + " не найден");
		return mpa.get();
	}

	public Collection<Mpa> getMpas() {
		return mpaRepository.getMpas();
	}
}
