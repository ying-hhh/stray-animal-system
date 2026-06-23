package org.example.animalsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.animalsystem.entity.Animal;
import org.example.animalsystem.mapper.AnimalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AnimalService {

    @Autowired
    private AnimalMapper animalMapper;

    public Page<Animal> searchAnimals(String name, String type, Integer page, Integer size) {
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Animal::getName, name);
        wrapper.eq(StringUtils.hasText(type), Animal::getType, type);
        wrapper.eq(Animal::getStatus, 1);
        wrapper.orderByDesc(Animal::getCreateTime);

        return animalMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Animal getAnimalById(Long id) {
        return animalMapper.selectById(id);
    }

    public boolean addAnimal(Animal animal) {
        animal.setStatus(1);
        return animalMapper.insert(animal) > 0;
    }

    public boolean updateAnimal(Animal animal) {
        return animalMapper.updateById(animal) > 0;
    }

    public boolean deleteAnimal(Long id) {
        Animal animal = new Animal();
        animal.setId(id);
        animal.setStatus(0);
        return animalMapper.updateById(animal) > 0;
    }
}